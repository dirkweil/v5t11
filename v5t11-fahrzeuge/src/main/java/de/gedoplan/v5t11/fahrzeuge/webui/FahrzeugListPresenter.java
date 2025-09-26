package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.baselibs.utils.util.ResourceUtil;
import de.gedoplan.baselibs.utils.xml.XmlConverter;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugTyp;
import de.gedoplan.v5t11.fahrzeuge.gateway.StatusGateway;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.constraints.NotNull;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.file.UploadedFile;

import lombok.Getter;
import lombok.Setter;

/**
 * Faces-Presenter für Fahrzeuge.
 * Dient als Unterstützung der View für die Fahrzeug-Liste
 * und bietet anderen Views Methoden zum Handling des aktuell ausgewählten Fahrzeugs an.
 */
@Named
@SessionScoped
public class FahrzeugListPresenter implements Serializable {

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Inject
  @RestClient
  StatusGateway statusGateway;

  @Inject
  Logger logger;

  @Inject
  Validator validator;

  @Getter
  private List<Fahrzeug> fahrzeuge;

  @Getter
  @Setter
  private Set<FahrzeugTyp> filter = Set.of(FahrzeugTyp.LOK);

  @Getter
  @Setter
  private Fahrzeug currentFahrzeug;

  @Getter
  @NotNull
  private FahrzeugId newId = new FahrzeugId(SystemTyp.DCC, 3);

  @PostConstruct
  void refreshFahrzeuge() {
    this.fahrzeuge = this.fahrzeugRepository.findAllSortedByBetriebsnummer();
  }

  public FahrzeugTyp[] getFahrzeugTypen() {
    return FahrzeugTyp.values();
  }

  public List<Fahrzeug> getFilteredFahrzeuge() {
    return this.fahrzeuge.stream()
      .filter(f -> this.filter.contains(f.getFahrzeugTyp()))
      .toList();
  }

  public String getImage(Fahrzeug fahrzeug) {
    if (fahrzeug.getImage() != null) {
      throw new UnsupportedOperationException("not yet implemented");
    }

    if (fahrzeug.getBetriebsnummer() != null) {
      String name = fahrzeug.getBetriebsnummer().replaceAll("\\s+", "_");
      while (!name.isEmpty()) {
        String resourceName = "images/loks/" + name + ".png";
        if (ResourceUtil.getResource("META-INF/resources/" + resourceName) != null) {
          return resourceName;
        }

        name = name.substring(0, name.length() - 1);
      }
    }

    return "images/loks/none.png";
  }

  public String getImageOfCurrentFahrzeug() {
    return getImage(this.currentFahrzeug);
  }

  public SystemTyp[] getSystemTypen() {
    return SystemTyp.values();
  }

  public String create() {
    this.currentFahrzeug = new Fahrzeug(new FahrzeugId(SystemTyp.DCC, 3));
    return "create";
  }

  public String saveCurrentFahrzeug() {
    if (this.fahrzeuge.stream()
      .filter(f -> !f.equals(this.currentFahrzeug))
      .anyMatch(f -> f.getBetriebsnummer().equals(this.currentFahrzeug.getBetriebsnummer()))) {
      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Betriebsnummer bereits vergeben", null);
      FacesContext.getCurrentInstance().addMessage(null, message);
      return null;
    }

    Set<ConstraintViolation<Fahrzeug>> violations = this.validator.validate(this.currentFahrzeug);
    if (!violations.isEmpty()) {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      violations.forEach(cv -> {
        FacesMessage facesMessage = new FacesMessage(cv.getMessage());
        facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
        facesContext.addMessage(null, facesMessage);
      });
      facesContext.validationFailed();
      return null;
    }

    Iterator<FahrzeugFunktion> iterator = this.currentFahrzeug.getFunktionen().iterator();
    while (iterator.hasNext()) {
      FahrzeugFunktion funktion = iterator.next();
      if (funktion.getMaske() == 0 || funktion.getBeschreibung() == null || funktion.getBeschreibung().strip().isEmpty()) {
        iterator.remove();
      }
    }

    this.fahrzeugRepository.merge(this.currentFahrzeug);
    refreshFahrzeuge();
    return "finished";
  }

  public String remove() {
    this.fahrzeugRepository.removeById(this.currentFahrzeug.getId());
    refreshFahrzeuge();
    return "finished";
  }

  public String cancel() {
    refreshFahrzeuge();
    return "finished";
  }

  public StreamedContent getXmlFile(Fahrzeug fahrzeug) {
    String filename = fahrzeug.getBetriebsnummer().replaceAll("[^a-zA-Z0-9-]", "_");
    try {
      String xmlString = XmlConverter.toXml(fahrzeug);
      return DefaultStreamedContent.builder()
        .name(filename)
        .contentType("application/xml")
        .stream(() -> new ByteArrayInputStream(xmlString.getBytes(StandardCharsets.UTF_8)))
        .build();
    } catch (Exception e) {
      return null;
    }
  }

  public void handleFileUpload(FileUploadEvent event) {
    UploadedFile file = event.getFile();
    logger.debugf("File %s hochgeladen", file.getFileName());

    try (Reader contentReader = new InputStreamReader(file.getInputStream())) {
      Fahrzeug fahrzeug = XmlConverter.fromXml(Fahrzeug.class, contentReader);
      logger.debugf("Hochgeladene Fahrzeugdaten: %s", fahrzeug);
      this.currentFahrzeug = fahrzeug;
    } catch (Exception e) {
      logger.error("File-Import fehlgeschlagen", e);
      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Upload fehlgeschlagen", null);
      FacesContext.getCurrentInstance().addMessage(null, message);
    }
  }
}

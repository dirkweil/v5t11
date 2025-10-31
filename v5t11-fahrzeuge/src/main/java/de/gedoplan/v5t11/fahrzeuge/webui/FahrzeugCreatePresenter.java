package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.baselibs.utils.xml.XmlConverter;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugTyp;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.jboss.logging.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.file.UploadedFile;

import lombok.Getter;

@Named
@ViewScoped
public class FahrzeugCreatePresenter implements Serializable {

  @Inject
  FahrzeugListPresenter fahrzeugListPresenter;

  @Inject
  Logger logger;

  @Getter
  private Fahrzeug newFahrzeug = Fahrzeug.builder()
    .fahrzeugTyp(FahrzeugTyp.LOK)
    .systemTyp(SystemTyp.DCC)
    .adresse(3)
    .build();

  public String createFahrzeug() {

    boolean duplicateBetriebsnummer = this.fahrzeugListPresenter
      .getFahrzeuge()
      .stream()
      .anyMatch(f -> f.getBetriebsnummer().equals(newFahrzeug.getBetriebsnummer()));
    if (duplicateBetriebsnummer) {
      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Betriebsnummer bereits vergeben", null);
      FacesContext.getCurrentInstance().addMessage(null, message);
      return null;
    }

    this.fahrzeugListPresenter.setCurrentFahrzeug(newFahrzeug);
    String outcome = this.fahrzeugListPresenter.saveCurrentFahrzeug();
    return "finished".equals(outcome) ? "inserted" : outcome;
  }

  public void handleFileUpload(FileUploadEvent event) {
    UploadedFile file = event.getFile();
    logger.debugf("File %s hochgeladen", file.getFileName());

    try (Reader contentReader = new InputStreamReader(file.getInputStream())) {
      newFahrzeug = XmlConverter.fromXml(Fahrzeug.class, contentReader);
      logger.debugf("Hochgeladene Fahrzeugdaten: %s", newFahrzeug);
    } catch (Exception e) {
      logger.error("File-Import fehlgeschlagen", e);
      FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Upload fehlgeschlagen", null);
      FacesContext.getCurrentInstance().addMessage(null, message);
    }
  }

}

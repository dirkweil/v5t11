package de.gedoplan.v5t11.fahrzeuge.webui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.jboss.logging.Logger;
import org.primefaces.PrimeFaces;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.service.GeschwindigkeitsMessService;
import de.gedoplan.v5t11.fahrzeuge.service.ParcoursService;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.ConverterException;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Named
@ViewScoped
public class FahrzeugMessungPresenter implements Serializable {

  @Inject
  FahrzeugListPresenter fahrzeugListPresenter;

  @Inject
  ParcoursService parcoursService;

  @Inject
  GeschwindigkeitsMessService geschwindigkeitsMessService;

  @Inject
  PushService pushService;

  @Getter
  private List<Gleis> gleise;

  @Inject
  Logger logger;

  @PostConstruct
  void init() {
    this.gleise = this.parcoursService
        .getGleise()
        .stream()
        .filter(g -> g.getLaenge() > 0)
        .filter(Gleis::isMessGleis)
        .toList();

    this.messGleis = this.gleise.isEmpty() ? null : this.gleise.getFirst();
    setEinUndAusfahrtsgleis();

    refreshGeschwindigkeiten();

    this.geschwindigkeitsMessService.attachObserver(this::updateUI);
  }

  @PreDestroy
  void cleanup() {
    this.geschwindigkeitsMessService.detachObserver();
  }

  public Fahrzeug getCurrentFahrzeug() {
    return this.fahrzeugListPresenter.getCurrentFahrzeug();
  }

  public String saveCurrentFahrzeug() {
    return this.fahrzeugListPresenter.saveCurrentFahrzeug();
  }

  public class GleisConverter implements Converter<Gleis> {

    @Override
    public Gleis getAsObject(FacesContext context, UIComponent component, String value) throws ConverterException {
      return value == null ? null : parcoursService.findGleisById(BereichselementId.fromString(value)).orElse(null);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Gleis value) throws ConverterException {
      return value == null ? null : value.getId().toString();
    }
  }

  @Getter
  private GleisConverter gleisConverter = new GleisConverter();

  @Getter
  private Gleis messGleis;

  public void setMessGleis(Gleis messGleis) {
    this.messGleis = messGleis;
    setEinUndAusfahrtsgleis();
  }

  @Getter
  @Setter
  private Gleis linksGleis;

  @Getter
  @Setter
  private Gleis rechtsGleis;

  private void setEinUndAusfahrtsgleis() {
    this.linksGleis = this.parcoursService.findGleisVor(this.messGleis);
    this.rechtsGleis = this.parcoursService.findGleisNach(this.messGleis);
  }

  private Runnable selectedAktion;

  @Getter
  private String selectedAktionsBeschreibung = "AAA";

  @Getter
  private String selectedAktionsAnleitung = "XXX";

  public void selectHoechstgeschwindigkeitMessen() {
    setSelectedAktion("Höchstgeschwindigkeit messen", "Messgleis und angrenzende Gleise räumen. Dann Fahrzeug das Messgleis mit Höchstgeschwindigkeit durchfahren lassen.",
        this::hoechstgeschwindigkeitMessen);
  }

  public void selectGeschwindigkeitsprofileErstellen() {
    setSelectedAktion("Geschwindigkeitsprofil erstellen", "Messgleis und angrenzende Gleise räumen. Fahrzeug so aufstellen, dass es in Richtung Messgleis fahren wird.",
        this::geschwindigkeitsprofileErstellen);
  }

  public void messungAbbrechen() {
    this.geschwindigkeitsMessService.abbrechen();
  }

  private void setSelectedAktion(String beschreibung, String anleitung, Runnable aktion) {
    this.selectedAktionsBeschreibung = beschreibung;
    this.selectedAktionsAnleitung = anleitung;
    this.selectedAktion = aktion;

    PrimeFaces.current().ajax().update(":fahrzeug-messung-confirm");
    PrimeFaces.current().executeScript("PF('fahrzeugMessungConfirm').show()");
  }

  public void execSelectedAktion() {
    try {
      this.selectedAktion.run();
      PrimeFaces.current().executeScript("PF('fahrzeugMessungConfirm').hide()");
      PrimeFaces.current().ajax().update(":fahrzeug-messung");
    } catch (Exception e) {
      String msg = String.format("Kann Aktion \"%s\" nicht durchführen", this.selectedAktionsBeschreibung);
      this.logger.error(msg, e);

      FacesContext facesContext = FacesContext.getCurrentInstance();
      FacesMessage facesMessage = new FacesMessage(msg + ": " + e);
      facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
      facesContext.addMessage(null, facesMessage);
    }

    PrimeFaces.current().ajax().update(":messages");
  }

  private void hoechstgeschwindigkeitMessen() {
    this.geschwindigkeitsMessService.startHoechstgeschwindigkeitsMessung(getCurrentFahrzeug());
  }

  private void geschwindigkeitsprofileErstellen() {
    this.geschwindigkeitsMessService.startProfilMessung(getCurrentFahrzeug());
  }

  public boolean isAktiv() {
    return this.geschwindigkeitsMessService.isAktiv();
  }

  public String getStatusDescription() {
    return this.geschwindigkeitsMessService.getStatusDescription();
  }

  private void updateUI() {
    refreshGeschwindigkeiten();
    this.pushService.somethingChanged();
  }

  private void refreshGeschwindigkeiten() {
    this.geschwindigkeiten = this.geschwindigkeitsMessService
        .getGeschwindigkeit()
        .keySet()
        .stream()
        .mapToInt(i -> Math.abs(i))
        .sorted()
        .distinct()
        .mapToObj(fahrstufe -> new GeschwindigkeitsEntry(
            fahrstufe,
            this.geschwindigkeitsMessService.getGeschwindigkeit().get(fahrstufe),
            this.geschwindigkeitsMessService.getGeschwindigkeit().get(-fahrstufe)))
        .toList();
  }

  @AllArgsConstructor
  @Getter
  public static class GeschwindigkeitsEntry {
    private int fahrstufe;
    private Long vorwaerts;
    private Long rueckwaerts;
  }

  @Getter
  private List<GeschwindigkeitsEntry> geschwindigkeiten = new ArrayList<>();

}

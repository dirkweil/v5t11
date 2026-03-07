package de.gedoplan.v5t11.fahrzeuge.webui;

import java.io.Serializable;
import java.util.SortedMap;

import org.jboss.logging.Logger;
import org.primefaces.PrimeFaces;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.service.GleisMessService;
import de.gedoplan.v5t11.fahrzeuge.service.ParcoursService;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;

@Named
@ViewScoped
public class GleisMessungPresenter implements Serializable {

  @Inject
  FahrzeugListPresenter fahrzeugListPresenter;

  @Inject
  ParcoursService parcoursService;

  @Inject
  GleisMessService gleisMessService;

  @Inject
  PushService pushService;

  @Inject
  Logger logger;

  @PostConstruct
  void init() {
    this.gleisMessService.attachObserver(this::updateUI);
  }

  @PreDestroy
  void cleanup() {
    this.gleisMessService.detachObserver();
  }

  public Fahrzeug getCurrentFahrzeug() {
    return this.fahrzeugListPresenter.getCurrentFahrzeug();
  }

  private Runnable selectedAktion;

  @Getter
  private String selectedAktionsBeschreibung = "AAA";

  @Getter
  private String selectedAktionsAnleitung = "XXX";

  public void selectGleislaengenMessen() {
    setSelectedAktion("Gleislängen messen", "Fahrzeug mit gleichbleibender mittlerer Geschwindigkeit zu messende Gleise durchfahren lassen.",
        this::gleislaengenMessen);
  }

  public void messungAbbrechen() {
    this.gleisMessService.abbrechen();
  }

  private void setSelectedAktion(String beschreibung, String anleitung, Runnable aktion) {
    this.selectedAktionsBeschreibung = beschreibung;
    this.selectedAktionsAnleitung = anleitung;
    this.selectedAktion = aktion;

    PrimeFaces.current().ajax().update(":gleis-messung-confirm");
    PrimeFaces.current().executeScript("PF('gleisMessungConfirm').show()");
  }

  public void execSelectedAktion() {
    try {
      this.selectedAktion.run();
      PrimeFaces.current().executeScript("PF('gleisMessungConfirm').hide()");
      PrimeFaces.current().ajax().update(":gleis-messung");
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

  private void gleislaengenMessen() {
    this.gleisMessService.startLaengenMessung(getCurrentFahrzeug());
  }

  public boolean isAktiv() {
    return this.gleisMessService.isAktiv();
  }

  public String getStatusDescription() {
    return this.gleisMessService.getStatusDescription();
  }

  private void updateUI() {
    this.pushService.somethingChanged();
  }

  public SortedMap<Gleis, Integer> getMessungen() {
    return this.gleisMessService.getMessungen();
  }

  public void removeMessung(Gleis gleis) {
    this.gleisMessService.removeMessung(gleis);
  }

  public void removeOverrides() {
    this.gleisMessService.removeOverrides();
  }

  public String save() {
    this.gleisMessService.save();
    return "finished";
  }

}

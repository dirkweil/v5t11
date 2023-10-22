package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugKonfiguration;
import de.gedoplan.v5t11.fahrzeuge.gateway.StatusGateway;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.util.cdi.Current;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.validation.Validator;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;
import org.primefaces.PrimeFaces;

import lombok.Getter;

@Named
@ViewScoped
public class FahrzeugProgramPresenter implements Serializable {

  @Inject
  @Current
  @Getter
  Fahrzeug currentFahrzeug;

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Inject
  @RestClient
  StatusGateway statusGateway;

  @Inject
  Logger log;

  @Inject
  Validator validator;

  private List<FahrzeugKonfiguration> selectedKonfigurationen;
  private Consumer<List<FahrzeugKonfiguration>> selectedAktion;
  @Getter
  private String selectedAktionsBeschreibung;

  public String save() {
    if (Set.copyOf(this.currentFahrzeug.getKonfigurationen()).size() != this.currentFahrzeug.getKonfigurationen().size()) {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, this.currentFahrzeug.getId().getSystemTyp().getKonfigWertBezeichnung() + " doppelt", null));
    } else {
      this.fahrzeugRepository.merge(this.currentFahrzeug);
    }

    this.currentFahrzeug.getKonfigurationen().sort((o1, o2) -> Integer.compare(o1.getNr(), o2.getNr()));

    return null;

  }

  public void addKonfiguration() {
    this.currentFahrzeug.getKonfigurationen().add(0, new FahrzeugKonfiguration(null, null, null));
  }

  public void removeKonfiguration(FahrzeugKonfiguration konfiguration) {
    Iterator<FahrzeugKonfiguration> iterator = this.currentFahrzeug.getKonfigurationen().iterator();
    while (iterator.hasNext()) {
      if (iterator.next() == konfiguration) {
        iterator.remove();
        break;
      }
    }
  }

  public void readAllKonfigurationen() {
    setSelectedAktion(this.currentFahrzeug.getKonfigurationen(), " aus dem Fahrzeug lesen", this::readKonfigurationen);
  }

  public void readKonfiguration(FahrzeugKonfiguration konfiguration) {
    setSelectedAktion(List.of(konfiguration), " aus dem Fahrzeug lesen", this::readKonfigurationen);
  }

  public void writeAllKonfigurationen() {
    setSelectedAktion(this.currentFahrzeug.getKonfigurationen(), " in das Fahrzeug schreiben", this::writeKonfigurationen);
  }

  public void writeKonfiguration(FahrzeugKonfiguration konfiguration) {
    setSelectedAktion(List.of(konfiguration), " in das Fahrzeug schreiben", this::writeKonfigurationen);
  }

  private void setSelectedAktion(List<FahrzeugKonfiguration> konfigurationen, String beschreibung, Consumer<List<FahrzeugKonfiguration>> aktion) {
    this.selectedKonfigurationen = konfigurationen;
    this.selectedAktion = aktion;
    this.selectedAktionsBeschreibung = konfigurationen
      .stream()
      .map(FahrzeugKonfiguration::getNr)
      .map(Object::toString)
      .collect(Collectors.joining(",", this.currentFahrzeug.getId().getSystemTyp().getKonfigWertBezeichnung() + " ", beschreibung));

    PrimeFaces.current().ajax().update(":fahrzeug-read-write-confirm");
    PrimeFaces.current().executeScript("PF('fahrzeugReadWriteConfirm').show()");
  }

  public void execSelectedAktion() {
    try {
      this.selectedAktion.accept(this.selectedKonfigurationen);
      PrimeFaces.current().executeScript("PF('fahrzeugReadWriteConfirm').hide()");
      PrimeFaces.current().ajax().update(":fahrzeug-config");
    } catch (Exception e) {
      String msg = String.format("Kann Aktion \"%s\" nicht durchführen", this.selectedAktionsBeschreibung);
      this.log.error(msg, e);

      FacesContext facesContext = FacesContext.getCurrentInstance();
      FacesMessage facesMessage = new FacesMessage(msg + ": " + e);
      facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
      facesContext.addMessage(null, facesMessage);
    }

    PrimeFaces.current().ajax().update(":messages");
  }

  private void readKonfigurationen(List<FahrzeugKonfiguration> konfigurationen) {
    List<Integer> keys = konfigurationen.stream().map(FahrzeugKonfiguration::getNr).toList();
    Map<Integer, Integer> result = this.statusGateway.getFahrzeugConfig(this.currentFahrzeug.getId().getSystemTyp(), keys);
    konfigurationen.forEach(k -> {
      Integer ist = result.get(k.getNr());
      if (ist != null && ist < 0) {
        ist = null;

        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage facesMessage = new FacesMessage(this.currentFahrzeug.getId().getSystemTyp().getKonfigWertBezeichnung() + " " + k.getNr() + " kann nicht gelesen werden");
        facesMessage.setSeverity(FacesMessage.SEVERITY_WARN);
        facesContext.addMessage(null, facesMessage);
      }
      k.setIst(ist);
    });
  }

  private void writeKonfigurationen(List<FahrzeugKonfiguration> konfigurationen) {
    Map<Integer, Integer> nrSollMap = konfigurationen.stream().collect(Collectors.toMap(FahrzeugKonfiguration::getNr, FahrzeugKonfiguration::getSoll));
    this.statusGateway.setFahrzeugConfig(this.currentFahrzeug.getId().getSystemTyp(), nrSollMap);
    konfigurationen.forEach(k -> k.setIst(k.getSoll()));
  }

  public void copyIst2SollAll() {
    copyIst2SollAll(this.currentFahrzeug.getKonfigurationen());
  }

  public void copyIst2Soll(FahrzeugKonfiguration konfiguration) {
    copyIst2SollAll(List.of(konfiguration));
  }

  private void copyIst2SollAll(List<FahrzeugKonfiguration> konfigurationen) {
    konfigurationen.stream().filter(k -> k.getIst() != null).forEach(k -> k.setSoll(k.getIst()));
  }

}

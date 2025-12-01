package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugKonfiguration;
import de.gedoplan.v5t11.fahrzeuge.gateway.StatusGateway;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;

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
  FahrzeugListPresenter fahrzeugListPresenter;

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

  public Fahrzeug getCurrentFahrzeug() {
    return this.fahrzeugListPresenter.getCurrentFahrzeug();
  }

  public String save() {
    // TODO
    if (Set.copyOf(getCurrentFahrzeug().getFahrzeugdecoder().getKonfigurationen()).size() != getCurrentFahrzeug().getFahrzeugdecoder().getKonfigurationen().size()) {
      FacesContext facesContext = FacesContext.getCurrentInstance();
      facesContext.addMessage(null,
        new FacesMessage(FacesMessage.SEVERITY_ERROR, getCurrentFahrzeug().getFahrzeugdecoder().getDecoderAdr().getSystemTyp().getKonfigWertBezeichnung() + " doppelt", null));
    } else {
      this.fahrzeugRepository.merge(getCurrentFahrzeug());
    }

    getCurrentFahrzeug().getFahrzeugdecoder().getKonfigurationen().sort((o1, o2) -> Integer.compare(o1.getNr(), o2.getNr()));

    return null;

  }

  public void addKonfiguration() {
    getCurrentFahrzeug().getFahrzeugdecoder().getKonfigurationen().add(0, new FahrzeugKonfiguration(null, null, null));
  }

  public void removeKonfiguration(FahrzeugKonfiguration konfiguration) {
    Iterator<FahrzeugKonfiguration> iterator = getCurrentFahrzeug().getFahrzeugdecoder().getKonfigurationen().iterator();
    while (iterator.hasNext()) {
      if (iterator.next() == konfiguration) {
        iterator.remove();
        break;
      }
    }
  }

  public void readAllKonfigurationen() {
    setSelectedAktion(getCurrentFahrzeug().getFahrzeugdecoder().getKonfigurationen(), " aus dem Fahrzeug lesen", this::readKonfigurationen);
  }

  public void readKonfiguration(FahrzeugKonfiguration konfiguration) {
    setSelectedAktion(List.of(konfiguration), " aus dem Fahrzeug lesen", this::readKonfigurationen);
  }

  public void writeAllKonfigurationen() {
    setSelectedAktion(getCurrentFahrzeug().getFahrzeugdecoder().getKonfigurationen(), " in das Fahrzeug schreiben", this::writeKonfigurationen);
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
      .collect(Collectors.joining(",", getCurrentFahrzeug().getFahrzeugdecoder().getDecoderAdr().getSystemTyp().getKonfigWertBezeichnung() + " ", beschreibung));

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
    Map<Integer, Integer> result = this.statusGateway.getFahrzeugdecoderConfig(getCurrentFahrzeug().getFahrzeugdecoder().getDecoderAdr().getSystemTyp(), keys);
    konfigurationen.forEach(k -> {
      Integer ist = result.get(k.getNr());
      if (ist != null && ist < 0) {
        ist = null;

        FacesContext facesContext = FacesContext.getCurrentInstance();
        FacesMessage facesMessage = new FacesMessage(
          getCurrentFahrzeug().getFahrzeugdecoder().getDecoderAdr().getSystemTyp().getKonfigWertBezeichnung() + " " + k.getNr() + " kann nicht gelesen werden");
        facesMessage.setSeverity(FacesMessage.SEVERITY_WARN);
        facesContext.addMessage(null, facesMessage);
      }
      k.setIst(ist);
    });
  }

  private void writeKonfigurationen(List<FahrzeugKonfiguration> konfigurationen) {
    Map<Integer, Integer> nrSollMap = konfigurationen.stream().collect(Collectors.toMap(FahrzeugKonfiguration::getNr, FahrzeugKonfiguration::getSoll));
    this.statusGateway.setFahrzeugdecoderConfig(getCurrentFahrzeug().getFahrzeugdecoder().getDecoderAdr().getSystemTyp(), nrSollMap);
    konfigurationen.forEach(k -> k.setIst(k.getSoll()));
  }

  public void copyIst2SollAll() {
    copyIst2SollAll(getCurrentFahrzeug().getFahrzeugdecoder().getKonfigurationen());
  }

  public void copyIst2Soll(FahrzeugKonfiguration konfiguration) {
    copyIst2SollAll(List.of(konfiguration));
  }

  private void copyIst2SollAll(List<FahrzeugKonfiguration> konfigurationen) {
    konfigurationen.stream().filter(k -> k.getIst() != null).forEach(k -> k.setSoll(k.getIst()));
  }

}

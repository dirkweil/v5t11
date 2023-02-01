package de.gedoplan.v5t11.leitstand.webui;

import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.Stellwerk;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkDkw2;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkEinfachWeiche;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkElement;
import de.gedoplan.v5t11.leitstand.entity.stellwerk.StellwerkGleis;
import de.gedoplan.v5t11.leitstand.gateway.FahrstrassenGateway;
import de.gedoplan.v5t11.leitstand.gateway.StatusGateway;
import de.gedoplan.v5t11.leitstand.persistence.SignalRepository;
import de.gedoplan.v5t11.leitstand.persistence.WeicheRepository;
import de.gedoplan.v5t11.leitstand.service.FahrstrassenManager;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenFilter;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Named
@ViewScoped
public class StellwerkPresenter implements Serializable {

  @Inject
  StellwerkSessionHolder stellwerkSessionHolder;

  @Inject
  SignalRepository signalRepository;

  @Inject
  WeicheRepository weicheRepository;

  @Inject
  @RestClient
  StatusGateway statusGateway;

  @Inject
  FahrstrassenManager fahrstrassenManager;

  @Inject
  @RestClient
  FahrstrassenGateway fahrstrassenGateway;

  @Inject
  StellwerkVorschlagService stellwerkVorschlagService;

  @Inject
  Logger logger;

  @Getter
  @Setter
  private String webSocketSessionId;

  @Getter
  private boolean controlPanelEnabled;

  private Gleis startGleis = null;
  private long startGleisTimeStamp = -1L;

  @Getter
  private BereichselementId weiche1Id;

  @Getter
  private BereichselementId weiche2Id;

  @Getter
  private BereichselementId signalId;

  @Getter
  private Fahrstrasse fahrstrasse;

  private List<Fahrstrasse> fahrstrassenVorschlaege = List.of();

  @Getter
  private boolean freigabeButtonEnabled;

  @Getter
  private boolean zugfahrtButtonEnabled;

  @Getter
  private boolean rangierfahrtEnabled;

  @PostConstruct
  void postConstruct() {
    String bereich = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("bereich");
    this.logger.debugf("bereich: %s", bereich);

    this.stellwerkSessionHolder.changeBereich(bereich);
  }

  public Stellwerk getStellwerk() {
    return this.stellwerkSessionHolder.getStellwerk();
  }

  public void elementClicked() {
    String uiId = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get("uiId");
    this.logger.tracef("elementClicked: uiId=%s, webSocketSessionId=%s", uiId, this.webSocketSessionId);

    StellwerkElement element = this.stellwerkSessionHolder.getElementByUiUd(uiId);
    if (element != null) {
      this.logger.tracef("element: %s %s", element.getClass().getSimpleName(), element.getId());

      //      clearControlPanel();
      this.weiche1Id = null;
      this.weiche2Id = null;
      this.signalId = null;

      if (element instanceof StellwerkGleis stellwerkGleis) {
        gleisClicked(stellwerkGleis.findGleis());
      } else if (element instanceof StellwerkEinfachWeiche stellwerkGleis) {
        gleisClicked(stellwerkGleis.findGleis());
        weicheClicked(stellwerkGleis.getId(), null);
      } else if (element instanceof StellwerkDkw2 stellwerkGleis) {
        gleisClicked(stellwerkGleis.findGleis());
        weicheClicked(stellwerkGleis.getWeicheAId(), stellwerkGleis.getWeicheBId());
      }

      signalClicked(element.getSignalId());
    }
  }

  public void clearControlPanel() {
    this.controlPanelEnabled = false;
    this.weiche1Id = null;
    this.weiche2Id = null;
    this.signalId = null;
    this.fahrstrasse = null;
    this.fahrstrassenVorschlaege = List.of();

    this.stellwerkVorschlagService.clear(this.webSocketSessionId);
  }

  private void weicheClicked(BereichselementId weiche1Id, BereichselementId weiche2Id) {
    this.weiche1Id = weiche1Id;
    this.weiche2Id = weiche2Id;

    this.controlPanelEnabled = true;
  }

  public WeichenStellung[] getWeichenStellungen() {
    return WeichenStellung.values();
  }

  public WeichenStellung getWeiche1Stellung() {
    return getWeicheXStellung(this.weiche1Id);
  }

  public void setWeiche1Stellung(WeichenStellung stellung) {
    setWeicheXStellung(this.weiche1Id, stellung);
  }

  public WeichenStellung getWeiche2Stellung() {
    return getWeicheXStellung(this.weiche2Id);
  }

  public void setWeiche2Stellung(WeichenStellung stellung) {
    setWeicheXStellung(this.weiche2Id, stellung);
  }

  private WeichenStellung getWeicheXStellung(BereichselementId weicheId) {
    if (weicheId != null) {
      Weiche weiche = this.weicheRepository.findById(weicheId);
      this.logger.debugf("getWeicheXStellung: %s", weiche);
      return weiche.getStellung();
    } else {
      return WeichenStellung.GERADE;
    }
  }

  private void setWeicheXStellung(BereichselementId weicheId, WeichenStellung stellung) {
    if (stellung != null && weicheId != null) {
      this.logger.debugf("setWeicheXStellung: %s -> %s", weicheId, stellung);
      try {
        this.statusGateway.weicheStellen(weicheId.getBereich(), weicheId.getName(), stellung);
      } catch (Exception e) {
        addFacesErrorMessage("Weiche kann nicht gestellt werden", "Status-Service", e);
      }
    }
  }

  private void signalClicked(BereichselementId signalId) {
    if (signalId != null) {
      this.logger.debugf("signalClicked: %s", signalId);

      this.signalId = signalId;

      this.controlPanelEnabled = true;
    }
  }

  public Set<SignalStellung> getSignalStellungen() {
    if (this.signalId != null) {
      return this.signalRepository.findById(this.signalId).getTyp().getErlaubteStellungen();
    } else {
      return Collections.emptySet();
    }

  }

  public SignalStellung getSignalStellung() {
    if (this.signalId != null) {
      Signal signal = this.signalRepository.findById(this.signalId);
      this.logger.debugf("getSignalStellung: %s", signal);
      return signal.getStellung();
    } else {
      return SignalStellung.HALT;
    }
  }

  public void setSignalStellung(SignalStellung stellung) {
    if (stellung != null && this.signalId != null) {
      this.logger.debugf("setSignalStellung: %s -> %s", this.signalId, stellung);
      try {
        this.statusGateway.signalStellen(this.signalId.getBereich(), this.signalId.getName(), stellung);
      } catch (Exception e) {
        addFacesErrorMessage("Signal kann nicht gestellt werden", "Status-Service", e);
      }
    }
  }

  private void addFacesErrorMessage(String msgText, String serviceName, Exception e) {
    this.logger.error(msgText, e);
    StringBuilder summary = new StringBuilder(msgText);
    if (e.getMessage().contains("Connection refused")) {
      summary.append("\n(");
      summary.append(serviceName);
      summary.append(" nicht erreichbar)");
    }

    addFacesMessage(summary, FacesMessage.SEVERITY_ERROR);

  }

  private static void addFacesMessage(CharSequence summary, FacesMessage.Severity severity) {
    FacesContext
      .getCurrentInstance()
      .addMessage(null, new FacesMessage(severity, summary.toString(), null));
  }

  private void gleisClicked(Gleis gleis) {
    this.logger.debugf("gleisClicked: %s", gleis.getId());

    long now = System.currentTimeMillis();

    Fahrstrasse fahrstrasse = this.fahrstrassenManager.getReservierteFahrstrasse(gleis);
    if (fahrstrasse != null) {
      // Gleis ist in aktiver Fahrstrasse
      this.logger.debugf("Aktive Fahrstrasse %s gewählt", fahrstrasse.getShortName());

      this.fahrstrasse = fahrstrasse;

      this.freigabeButtonEnabled = true;
      this.zugfahrtButtonEnabled = false;
      this.rangierfahrtEnabled = false;

      this.controlPanelEnabled = true;
      return;
    }

    fahrstrasse = this.fahrstrassenVorschlaege
      .stream()
      .filter(fs -> fs.getElement(gleis, false) != null)
      .findFirst()
      .orElse(null);
    if (fahrstrasse != null) {
      // Gleis ist in vorgeschlagener Fahrstrasse
      this.logger.debugf("Vergeschlagene Fahrstrasse %s gewählt", fahrstrasse.getShortName());

      this.fahrstrasse = fahrstrasse;

      this.stellwerkVorschlagService.set(this.webSocketSessionId, this.fahrstrasse, this.fahrstrassenVorschlaege);

      this.freigabeButtonEnabled = false;
      this.zugfahrtButtonEnabled = true;
      this.rangierfahrtEnabled = true;

      this.controlPanelEnabled = true;
      return;
    }

    if (this.startGleis == null || (now - this.startGleisTimeStamp) > 5000) {
      // Erstes Gleis innerhalb der Wartezeit => als Start einer Fahrstrasse merken
      this.logger.debugf("Gleis %s als möglichen FS-Beginn merken", gleis.getId());

      this.startGleisTimeStamp = now;
      this.startGleis = gleis;

      return;
    }

    // Zweites Gleis innerhalb Wartezeit => Fahrstrassen suchen
    this.logger.debugf("Fahrstrassen von %s nach %s suchen", this.startGleis.getId(), gleis.getId());

    try {
      List<Fahrstrasse> fahrstrassen = this.fahrstrassenGateway.getFahrstrassen(
        this.startGleis.getBereich(), this.startGleis.getName(),
        gleis.getBereich(), gleis.getName(),
        FahrstrassenFilter.FREI);

      this.logger.debugf("%d Fahrstrassen gefunden", fahrstrassen.size());

      if (fahrstrassen.isEmpty()) {
        addFacesMessage(String.format("Keine Fahrstrasse von %s nach %s gefunden", this.startGleis.getName(), gleis.getName()), FacesMessage.SEVERITY_WARN);
      } else {
        this.fahrstrassenVorschlaege = fahrstrassen;
        this.fahrstrasse = fahrstrassen.get(0);

        this.stellwerkVorschlagService.set(this.webSocketSessionId, this.fahrstrasse, this.fahrstrassenVorschlaege);

        this.freigabeButtonEnabled = false;
        this.zugfahrtButtonEnabled = true;
        this.rangierfahrtEnabled = true;

        this.controlPanelEnabled = true;
      }
    } catch (Exception e) {
      addFacesErrorMessage("Fahrstrassen können nicht ermittelt werden", "Fahrstrassen-Service", e);
    }
  }

  public void fahrstrasseReservierenZugfahrt() {
    fahrstrasseReservieren(FahrstrassenReservierungsTyp.ZUGFAHRT);
  }

  public void fahrstrasseReservierenRangierfahrt() {
    fahrstrasseReservieren(FahrstrassenReservierungsTyp.RANGIERFAHRT);
  }

  public void fahrstrasseFreigeben() {
    fahrstrasseReservieren(FahrstrassenReservierungsTyp.UNRESERVIERT);
  }

  public void fahrstrasseReservieren(FahrstrassenReservierungsTyp fahrstrassenReservierungsTyp) {
    Fahrstrasse fahrstrasse = this.fahrstrasse;

    clearControlPanel();

    if (fahrstrasse != null) {
      try {
        this.fahrstrassenGateway.reserviereFahrstrasse(fahrstrasse.getId(), fahrstrassenReservierungsTyp);
      } catch (Exception e) {
        String operation = fahrstrassenReservierungsTyp == FahrstrassenReservierungsTyp.UNRESERVIERT ? "freigegeben" : "reserviert";
        addFacesErrorMessage(String.format("Fahrstrasse %s kann nicht %s werden", fahrstrasse.getShortName(), operation), "Fahrstrassen-Service", e);
      }
    }
  }
}

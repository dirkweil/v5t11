package de.gedoplan.v5t11.status.webui;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Besetztmelder;
import de.gedoplan.v5t11.status.entity.baustein.zentrale.DummyZentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.lok.Lok;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

import lombok.Getter;
import lombok.Setter;

@SessionScoped
@Model
public class WebControlModel implements Serializable {
  @Getter
  private List<String> bereiche;

  @Getter
  @Setter
  private String bereich;

  @Getter
  @Setter
  private String lokId;

  @Getter
  @Setter
  private boolean lokAktiv;

  @Getter
  @Setter
  private int lokFahrstufe;

  @Getter
  @Setter
  private boolean lokRueckwaerts;

  @Getter
  @Setter
  private String weichenName;

  @Getter
  @Setter
  private String signalName;

  @Getter
  @Setter
  private String gleisabschnittsName;

  @Inject
  private Steuerung steuerung;

  @PostConstruct
  private void postConstruct() {
    this.bereiche = new ArrayList<>(this.steuerung.getBereiche());
    if (!this.bereiche.isEmpty()) {
      this.bereich = this.bereiche.get(0);
    }
  }

  public boolean isSelectrixSimuliert() {
    return this.steuerung.getZentrale() instanceof DummyZentrale;
  }

  public void gleisspannungEinschalten() {
    this.steuerung.getZentrale().setGleisspannung(true);
  }

  public void gleisspannungAusschalten() {
    this.steuerung.getZentrale().setGleisspannung(false);
  }

  public void weicheGeradeStellen() {
    weicheStellen(WeichenStellung.GERADE);
  }

  public void weicheAbzweigendStellen() {
    weicheStellen(WeichenStellung.ABZWEIGEND);
  }

  private void weicheStellen(WeichenStellung stellung) {
    Weiche weiche = this.steuerung.getWeiche(this.bereich, this.weichenName);
    if (weiche == null) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekannte Weiche"));
      return;
    }
    weiche.setStellung(stellung);
  }

  public void signalHaltStellen() {
    signalStellen(SignalStellung.HALT);
  }

  public void signalFahrtStellen() {
    signalStellen(SignalStellung.FAHRT);
  }

  public void signalLangsamfahrtStellen() {
    signalStellen(SignalStellung.LANGSAMFAHRT);
  }

  public void signalRangierfahrtStellen() {
    signalStellen(SignalStellung.RANGIERFAHRT);
  }

  private void signalStellen(SignalStellung stellung) {
    Signal signal = this.steuerung.getSignal(this.bereich, this.signalName);
    if (signal == null) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekanntes Signal"));
      return;
    }
    signal.setStellung(stellung);
  }

  public void lokAktivieren() {
    lokAktivSetzen(true);
  }

  public void lokDeaktivieren() {
    lokAktivSetzen(false);
  }

  private void lokAktivSetzen(boolean b) {
    Lok lok = this.steuerung.getLok(this.lokId);
    if (lok == null) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekannte Lok"));
      return;
    }
    lok.setAktiv(b);
  }

  public void lokLichtAusStellen() {
    lokLichtStellen(false);
  }

  public void lokLichtAnStellen() {
    lokLichtStellen(true);
  }

  private void lokLichtStellen(boolean b) {
    Lok lok = this.steuerung.getLok(this.lokId);
    if (lok == null) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekannte Lok"));
      return;
    }
    lok.setLicht(b);
  }

  public void lokFahrstufeStellen() {
    Lok lok = this.steuerung.getLok(this.lokId);
    if (lok == null) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekannte Lok"));
      return;
    }
    lok.setFahrstufe(this.lokFahrstufe);
    lok.setRueckwaerts(this.lokRueckwaerts);
  }

  public void lokStopp() {
    Lok lok = this.steuerung.getLok(this.lokId);
    if (lok == null) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekannte Lok"));
      return;
    }

    lok.setFahrstufe(0);
    this.lokFahrstufe = 0;
  }

  public void lokFahrstufeErhoehen() {
    Lok lok = this.steuerung.getLok(this.lokId);
    if (lok == null) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekannte Lok"));
      return;
    }

    int fahrstufe = lok.getFahrstufe();
    if (fahrstufe < lok.getMaxFahrstufe()) {
      ++fahrstufe;
    }
    lok.setFahrstufe(fahrstufe);
    this.lokFahrstufe = fahrstufe;

    this.lokRueckwaerts = lok.isRueckwaerts();
  }

  public void lokFahrstufeVermindern() {
    Lok lok = this.steuerung.getLok(this.lokId);
    if (lok == null) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekannte Lok"));
      return;
    }

    int fahrstufe = lok.getFahrstufe();
    if (fahrstufe > 0) {
      --fahrstufe;
    }
    lok.setFahrstufe(fahrstufe);
    this.lokFahrstufe = fahrstufe;

    this.lokRueckwaerts = lok.isRueckwaerts();
  }

  public void gleisBelegtSetzen() {
    gleisBelegtSetzen(true);
  }

  private void gleisBelegtSetzen(boolean besetzt) {
    Gleisabschnitt gleisabschnitt = this.steuerung.getGleisabschnitt(this.bereich, this.gleisabschnittsName);
    if (gleisabschnitt == null) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekannter Gleisabschnitt"));
      return;
    }

    Besetztmelder besetztmelder = gleisabschnitt.getBesetztmelder();
    int anschluss = gleisabschnitt.getAnschluss();
    long mask = 1L << anschluss;
    if (besetzt) {
      besetztmelder.setWert(besetztmelder.getWert() | mask);
    } else {
      besetztmelder.setWert(besetztmelder.getWert() & ~mask);
    }
  }

  public void gleisFreiSetzen() {
    gleisBelegtSetzen(false);
  }
}

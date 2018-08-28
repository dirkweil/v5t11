package de.gedoplan.v5t11.status.webui;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Besetztmelder;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.lok.Lok;
import de.gedoplan.v5t11.util.domain.SignalStellung;
import de.gedoplan.v5t11.util.domain.WeichenStellung;

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
  private int lokGeschwindigkeit;

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
    String portName = System.getProperty("v5t11.portName");
    return portName == null || portName.equalsIgnoreCase("none");
  }

  public void zentraleEinschalten() {
    this.steuerung.getZentrale().setAktiv(true);
  }

  public void zentraleAusschalten() {
    this.steuerung.getZentrale().setAktiv(false);
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

  public void lichtAusStellen() {
    lichtStellen(false);
  }

  public void lichtAnStellen() {
    lichtStellen(true);
  }

  private void lichtStellen(boolean b) {
    Lok lok = this.steuerung.getLok(this.lokId);
    if (lok == null) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekannte Lok"));
      return;
    }
    lok.setLicht(b);
  }

  public void geschwindigkeitStellen() {
    Lok lok = this.steuerung.getLok(this.lokId);
    if (lok == null) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekannte Lok"));
      return;
    }
    lok.setGeschwindigkeit(this.lokGeschwindigkeit);
    lok.setRueckwaerts(this.lokRueckwaerts);
  }

  public void lokStopp() {
    Lok lok = this.steuerung.getLok(this.lokId);
    if (lok == null) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekannte Lok"));
      return;
    }

    lok.setGeschwindigkeit(0);
    this.lokGeschwindigkeit = 0;
  }

  public void geschwindigkeitErhöhen() {
    Lok lok = this.steuerung.getLok(this.lokId);
    if (lok == null) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekannte Lok"));
      return;
    }

    int geschwindigkeit = lok.getGeschwindigkeit();
    if (geschwindigkeit < Lok.MAX_GESCHWINDIGKEIT) {
      ++geschwindigkeit;
    }
    lok.setGeschwindigkeit(geschwindigkeit);
    this.lokGeschwindigkeit = geschwindigkeit;

    this.lokRueckwaerts = lok.isRueckwaerts();
  }

  public void geschwindigkeitVermindern() {
    Lok lok = this.steuerung.getLok(this.lokId);
    if (lok == null) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekannte Lok"));
      return;
    }

    int geschwindigkeit = lok.getGeschwindigkeit();
    if (geschwindigkeit > 0) {
      --geschwindigkeit;
    }
    lok.setGeschwindigkeit(geschwindigkeit);
    this.lokGeschwindigkeit = geschwindigkeit;

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

package de.gedoplan.v5t11.status.webui;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Besetztmelder;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.SessionScoped;
import javax.enterprise.inject.Model;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;

@SessionScoped
@Model
public class WebControlModel implements Serializable {
  private List<String> bereiche;
  private String bereich;
  private int lokAdresse;
  private int lokGeschwindigkeit;
  private boolean lokRueckwaerts;
  private String weichenName;
  private String signalName;
  private String gleisabschnittsName;

  @Inject
  private Steuerung steuerung;

  @SuppressWarnings("unused")
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

  public List<String> getBereiche() {
    return this.bereiche;
  }

  /**
   * Wert liefern: {@link #bereich}.
   *
   * @return Wert
   */
  public String getBereich() {
    return this.bereich;
  }

  /**
   * Wert setzen: {@link #bereich}.
   *
   * @param bereich
   *          Wert
   */
  public void setBereich(String bereich) {
    this.bereich = bereich;
  }

  /**
   * Wert liefern: {@link #weichenName}.
   *
   * @return Wert
   */
  public String getWeichenName() {
    return this.weichenName;
  }

  /**
   * Wert setzen: {@link #weichenName}.
   *
   * @param weichenName
   *          Wert
   */
  public void setWeichenName(String weichenName) {
    this.weichenName = weichenName;
  }

  /**
   * Wert liefern: {@link #signalName}.
   *
   * @return Wert
   */
  public String getSignalName() {
    return this.signalName;
  }

  /**
   * Wert setzen: {@link #signalName}.
   *
   * @param signalName
   *          Wert
   */
  public void setSignalName(String signalName) {
    this.signalName = signalName;
  }

  /**
   * Wert liefern: {@link #lokAdresse}.
   *
   * @return Wert
   */
  public int getLokAdresse() {
    return this.lokAdresse;
  }

  /**
   * Wert setzen: {@link #lokAdresse}.
   *
   * @param lokAdresse
   *          Wert
   */
  public void setLokAdresse(int lokAdresse) {
    this.lokAdresse = lokAdresse;
  }

  /**
   * Wert liefern: {@link #lokGeschwindigkeit}.
   *
   * @return Wert
   */
  public int getLokGeschwindigkeit() {
    return this.lokGeschwindigkeit;
  }

  /**
   * Wert setzen: {@link #lokGeschwindigkeit}.
   *
   * @param lokGeschwindigkeit
   *          Wert
   */
  public void setLokGeschwindigkeit(int lokGeschwindigkeit) {
    this.lokGeschwindigkeit = lokGeschwindigkeit;
  }

  /**
   * Wert liefern: {@link #lokRueckwaerts}.
   *
   * @return Wert
   */
  public boolean isLokRueckwaerts() {
    return this.lokRueckwaerts;
  }

  /**
   * Wert setzen: {@link #lokRueckwaerts}.
   *
   * @param lokRueckwaerts
   *          Wert
   */
  public void setLokRueckwaerts(boolean lokRueckwaerts) {
    this.lokRueckwaerts = lokRueckwaerts;
  }

  public void zentraleEinschalten() {
    this.steuerung.getZentrale().setAktiv(true);
  }

  public void zentraleAusschalten() {
    this.steuerung.getZentrale().setAktiv(false);
  }

  public void weicheGeradeStellen() {
    weicheStellen(Weiche.Stellung.GERADE);
  }

  public void weicheAbzweigendStellen() {
    weicheStellen(Weiche.Stellung.ABZWEIGEND);
  }

  private void weicheStellen(Weiche.Stellung stellung) {
    Weiche weiche = this.steuerung.getWeiche(this.bereich, this.weichenName);
    if (weiche == null) {
      FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekannte Weiche"));
      return;
    }
    weiche.setStellung(stellung);
  }

  public void signalHaltStellen() {
    signalStellen(Signal.Stellung.HALT);
  }

  public void signalFahrtStellen() {
    signalStellen(Signal.Stellung.FAHRT);
  }

  public void signalLangsamfahrtStellen() {
    signalStellen(Signal.Stellung.LANGSAMFAHRT);
  }

  public void signalRangierfahrtStellen() {
    signalStellen(Signal.Stellung.RANGIERFAHRT);
  }

  private void signalStellen(Signal.Stellung stellung) {
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
    // Lok lok = this.steuerung.getLok(this.lokAdresse);
    // if (lok == null) {
    // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekannte Lok"));
    // return;
    // }
    // lok.setLicht(b);
  }

  public void geschwindigkeitStellen() {
    // Lok lok = this.steuerung.getLok(this.lokAdresse);
    // if (lok == null) {
    // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekannte Lok"));
    // return;
    // }
    // lok.setGeschwindigkeit(this.lokGeschwindigkeit);
    // lok.setRueckwaerts(this.lokRueckwaerts);
  }

  public void lokStopp() {
    // Lok lok = this.steuerung.getLok(this.lokAdresse);
    // if (lok == null) {
    // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekannte Lok"));
    // return;
    // }
    //
    // lok.setGeschwindigkeit(0);
    // this.lokGeschwindigkeit = 0;
  }

  public void geschwindigkeitErhöhen() {
    // Lok lok = this.steuerung.getLok(this.lokAdresse);
    // if (lok == null) {
    // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekannte Lok"));
    // return;
    // }
    //
    // int geschwindigkeit = lok.getGeschwindigkeit();
    // if (geschwindigkeit < Lok.MAX_GESCHWINDIGKEIT) {
    // ++geschwindigkeit;
    // }
    // lok.setGeschwindigkeit(geschwindigkeit);
    // this.lokGeschwindigkeit = geschwindigkeit;
    //
    // this.lokRueckwaerts = lok.isRueckwaerts();
  }

  public void geschwindigkeitVermindern() {
    // Lok lok = this.steuerung.getLok(this.lokAdresse);
    // if (lok == null) {
    // FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("unbekannte Lok"));
    // return;
    // }
    //
    // int geschwindigkeit = lok.getGeschwindigkeit();
    // if (geschwindigkeit > 0) {
    // --geschwindigkeit;
    // }
    // lok.setGeschwindigkeit(geschwindigkeit);
    // this.lokGeschwindigkeit = geschwindigkeit;
    //
    // this.lokRueckwaerts = lok.isRueckwaerts();
  }

  /**
   * Wert liefern: {@link #gleisabschnittsName}.
   *
   * @return Wert
   */
  public String getGleisabschnittsName() {
    return this.gleisabschnittsName;
  }

  /**
   * Wert setzen: {@link #gleisabschnittsName}.
   *
   * @param gleisAbschnittsName
   *          Wert
   */
  public void setGleisabschnittsName(String gleisAbschnittsName) {
    this.gleisabschnittsName = gleisAbschnittsName;
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

package de.gedoplan.v5t11.leitstand.entity.fahrstrasse;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import de.gedoplan.v5t11.util.domain.entity.Bereichselement;
import de.gedoplan.v5t11.util.domain.entity.Fahrwegelement;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import lombok.Getter;
import lombok.Setter;

public class Fahrstrassenelement extends Bereichselement {

  @Getter(onMethod_ = @JsonbInclude(full = true))
  @Setter(onMethod_ = @JsonbInclude(full = true))
  protected boolean zaehlrichtung;

  @Getter(onMethod_ = @JsonbInclude(full = true))
  @Setter(onMethod_ = @JsonbInclude(full = true))
  protected boolean schutz;

  @Getter(onMethod_ = @JsonbInclude(full = true))
  @Setter(onMethod_ = @JsonbInclude(full = true))
  protected String typ;

  @Getter(onMethod_ = @JsonbInclude(full = true))
  @Setter(onMethod_ = @JsonbInclude(full = true))
  protected String stellung;

  @Getter
  private Fahrwegelement fahrwegelement;

  /**
   * Zugehöriges Fahrwegelement suchen und eintragen.
   *
   * @param leitstand
   *        Parcours
   */
  public void linkFahrwegelement(Leitstand leitstand) {
    switch (this.typ) {
    case "GLEIS":
      // TODO Gleisabschnitt
      // this.fahrwegelement = leitstand.getGleisabschnitt(getBereich(), getName());
      break;

    case "SIGNAL":
      // TODO Signal
      // this.fahrwegelement = leitstand.getSignal(getBereich(), getName());
      break;

    case "WEICHE":
      // TODO Weiche
      // this.fahrwegelement = leitstand.getWeiche(getBereich(), getName());
      break;
    }
  }

  @Override
  public String toString() {
    return (this.fahrwegelement != null
        ? this.fahrwegelement.toString()
        : "typ=" + this.typ + ", bereich=" + getBereich() + ", name=" + getName())
        + ", zaehlrichtung=" + this.zaehlrichtung + ", schutz=" + this.schutz + ", stellung=" + this.stellung;
  }

  /**
   * Conveniance-Methode: Zugeordnete Weichenstellung liefern.
   * Darf nur aufgerufen werden, wenn es sich um ein Weichenelement handelt!
   *
   * @return Weichenstellung
   */
  public WeichenStellung getWeichenstellung() {
    return WeichenStellung.valueOf(this.stellung);
  }

  /**
   * Conveniance-Methode: Zugeordnete Signalstellung liefern.
   * Darf nur aufgerufen werden, wenn es sich um ein Signalelement handelt!
   *
   * @return Signalstellung
   */
  public SignalStellung getSignalstellung() {
    return SignalStellung.valueOf(this.stellung);
  }

}

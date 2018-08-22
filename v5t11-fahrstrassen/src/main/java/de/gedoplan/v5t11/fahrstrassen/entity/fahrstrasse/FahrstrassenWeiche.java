package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.util.domain.WeichenStellung;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class FahrstrassenWeiche extends FahrstrassenGeraet {

  @Getter
  private Weiche weiche;

  @Getter
  @XmlAttribute
  private WeichenStellung stellung;

  @Override
  public Weiche getFahrwegelement() {
    return this.weiche;
  }

  public FahrstrassenGleisabschnitt createFahrstrassenGleisabschnitt() {

    /*
     * Konvention: Eine Doppelweiche (z. B. DKW) besteht aus zwei Einzelweichen, deren Namen
     * mit der gleichen Nummer beginnen und einen eindeutigen Suffixbuchstaben haben (z. B. 11a, 11b).
     * Der zugehörige Gleisabschnitt enthält nur den numerischen Teil im Namen.
     */
    boolean doppelweiche = Character.isAlphabetic(this.name.charAt(this.name.length() - 1));
    String name;
    if (doppelweiche) {
      name = FahrstrassenGleisabschnitt.PREFIX_WEICHEN_GLEISABSCHNITT + this.name.substring(0, this.name.length() - 1);
    } else {
      name = FahrstrassenGleisabschnitt.PREFIX_WEICHEN_GLEISABSCHNITT + this.name;
    }

    return new FahrstrassenGleisabschnitt(this.bereich, name, this.zaehlrichtung);
  }

  @Override
  public void linkFahrwegelement(Parcours parcours) {
    this.weiche = parcours.getWeiche(this.bereich, this.name);
    if (this.weiche == null) {
      this.weiche = new Weiche(this.bereich, this.name);
      parcours.addWeiche(this.weiche);
    }
  }

  @Override
  public int getRank() {
    return this.stellung == WeichenStellung.ABZWEIGEND ? 1 : 0;
  }

  @Override
  public String toString() {
    return super.toString() + ", stellung=" + this.stellung;
  }
}

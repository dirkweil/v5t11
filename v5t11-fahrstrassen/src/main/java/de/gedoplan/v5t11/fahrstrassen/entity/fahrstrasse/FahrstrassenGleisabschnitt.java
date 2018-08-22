package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class FahrstrassenGleisabschnitt extends Fahrstrassenelement {

  public static final String PREFIX_WEICHEN_GLEISABSCHNITT = "W";

  @Getter
  @Setter
  private Gleisabschnitt gleisabschnitt;

  public FahrstrassenGleisabschnitt(String bereich, String name, boolean zaehlrichtung) {
    super(bereich, name, zaehlrichtung);
  }

  @Override
  public Gleisabschnitt getFahrwegelement() {
    return this.gleisabschnitt;
  }

  public boolean isWeichenGleisabschnitt() {
    return this.name.startsWith(PREFIX_WEICHEN_GLEISABSCHNITT);
  }

  @Override
  public void linkFahrwegelement(Parcours parcours) {
    this.gleisabschnitt = parcours.getGleisabschnitt(this.bereich, this.name);
    if (this.gleisabschnitt == null) {
      this.gleisabschnitt = new Gleisabschnitt(this.bereich, this.name);
      parcours.addGleisabschnitt(this.gleisabschnitt);
    }
  }

  @Override
  public int getRank() {
    return 1;
  }

}

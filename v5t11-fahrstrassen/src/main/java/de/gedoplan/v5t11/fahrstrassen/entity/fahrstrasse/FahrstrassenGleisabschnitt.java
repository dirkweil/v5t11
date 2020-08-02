package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractWeiche;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class FahrstrassenGleisabschnitt extends Fahrstrassenelement {

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
    return getName().startsWith(AbstractWeiche.PREFIX_WEICHEN_GLEISABSCHNITT);
  }

  @Override
  public void linkFahrwegelement(Parcours parcours) {
    this.gleisabschnitt = parcours.getGleisabschnitt(getBereich(), getName());
    if (this.gleisabschnitt == null) {
      this.gleisabschnitt = new Gleisabschnitt(getBereich(), getName());
      parcours.addGleisabschnitt(this.gleisabschnitt);
    }
  }

  @Override
  public int getRank() {
    return 1;
  }

  @Override
  public void reservieren(Fahrstrasse fahrstrasse) {
    if (this.gleisabschnitt != null) {
      this.gleisabschnitt.reserviereFuerFahrstrasse(fahrstrasse, isZaehlrichtung());
    }
  }

  @Override
  @JsonbInclude(full = true)
  public String getTyp() {
    return "GLEIS";
  }
}

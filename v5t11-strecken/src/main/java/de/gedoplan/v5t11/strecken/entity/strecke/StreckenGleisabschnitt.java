package de.gedoplan.v5t11.strecken.entity.strecke;

import de.gedoplan.v5t11.strecken.entity.Parcours;
import de.gedoplan.v5t11.strecken.entity.fahrweg.Gleisabschnitt;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class StreckenGleisabschnitt extends Streckenelement {

  @Getter
  @Setter
  private Gleisabschnitt gleisabschnitt;

  @Override
  public Gleisabschnitt getFahrwegelement() {
    return this.gleisabschnitt;
  }

  public StreckenGleisabschnitt(String bereich, String name) {
    this.bereich = bereich;
    this.name = name;
  }

  @Override
  public void linkFahrwegelement(Parcours parcours) {
    this.gleisabschnitt = parcours.getGleisabschnitt(this.bereich, this.name);
    if (this.gleisabschnitt == null) {
      this.gleisabschnitt = new Gleisabschnitt(this.bereich, this.name);
      parcours.addGleisabschnitt(this.gleisabschnitt);
    }
  }
}

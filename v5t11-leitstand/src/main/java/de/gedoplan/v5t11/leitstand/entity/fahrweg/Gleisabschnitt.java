package de.gedoplan.v5t11.leitstand.entity.fahrweg;

import de.gedoplan.v5t11.util.domain.entity.fahrweg.AbstractGleisabschnitt;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class Gleisabschnitt extends AbstractGleisabschnitt {

  public Gleisabschnitt(String bereich, String name) {
    super(bereich, name);
  }

  public synchronized void copyStatus(Gleisabschnitt other) {
    this.besetzt = other.besetzt;
  }

}

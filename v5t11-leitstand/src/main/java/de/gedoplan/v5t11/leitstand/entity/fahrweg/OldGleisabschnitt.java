package de.gedoplan.v5t11.leitstand.entity.fahrweg;

import de.gedoplan.v5t11.util.domain.entity.fahrweg.AbstractGleisabschnitt;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class OldGleisabschnitt extends AbstractGleisabschnitt implements OldStatusUpdateable<OldGleisabschnitt> {

  public OldGleisabschnitt(String bereich, String name) {
    super(bereich, name);
  }

  public synchronized void copyStatus(OldGleisabschnitt other) {
    setBesetzt(other.isBesetzt());
  }

}

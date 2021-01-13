package de.gedoplan.v5t11.fahrzeuge.entity.fahrweg;

import de.gedoplan.v5t11.util.domain.entity.fahrweg.AbstractGleis;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class Gleis extends AbstractGleis implements StatusUpdateable<Gleis> {

  public Gleis(String bereich, String name) {
    super(bereich, name);
  }

  public synchronized void copyStatus(Gleis other) {
    setBesetzt(other.isBesetzt());
  }

}

package de.gedoplan.v5t11.fahrzeuge.entity.fahrweg;

import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractWeiche;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class Weiche extends AbstractWeiche implements StatusUpdateable<Weiche> {

  public Weiche(String bereich, String name) {
    super(bereich, name);
  }

  public synchronized void copyStatus(Weiche other) {
    setStellung(other.getStellung());
  }

}

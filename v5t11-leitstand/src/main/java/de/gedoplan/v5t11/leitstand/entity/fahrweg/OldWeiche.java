package de.gedoplan.v5t11.leitstand.entity.fahrweg;

import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractWeiche;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.NoArgsConstructor;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class OldWeiche extends AbstractWeiche implements OldStatusUpdateable<OldWeiche> {

  public OldWeiche(String bereich, String name) {
    super(bereich, name);
  }

  public synchronized void copyStatus(OldWeiche other) {
    setStellung(other.getStellung());
  }

}

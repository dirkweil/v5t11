package de.gedoplan.v5t11.leitstand.entity.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.OldGleisabschnitt;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;

/**
 * Leeres Stellwerkselement
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public class StellwerkGleisabschnitt extends StellwerkElement {

  @XmlAttribute
  @Getter
  boolean label;

  @Getter
  private OldGleisabschnitt gleisabschnitt;

  @Override
  public String toString() {
    return super.toString() + " label=" + this.label;
  }

  @Override
  public void linkFahrwegelemente(Leitstand leitstand) {
    super.linkFahrwegelemente(leitstand);
    this.gleisabschnitt = leitstand.getOrCreateGleisabschnitt(this.bereich, this.name);
  }

}

package de.gedoplan.v5t11.leitstand.entity.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Getter;

/**
 * Leeres Stellwerkselement
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public class StellwerkWeiche extends StellwerkElement {

  @Getter
  private Weiche weiche;

  @Getter
  private Gleisabschnitt gleisabschnitt;

  @Override
  public void linkFahrwegelemente(Leitstand leitstand) {
    super.linkFahrwegelemente(leitstand);
    this.weiche = leitstand.getOrCreateWeiche(this.bereich, this.name);
    this.gleisabschnitt = leitstand.getOrCreateGleisabschnitt(this.bereich, this.weiche.getGleisabschnittName());
  }

}

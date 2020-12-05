package de.gedoplan.v5t11.leitstand.entity.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.OldGleisabschnitt;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.OldWeiche;

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
  private OldWeiche weiche;

  @Getter
  private OldGleisabschnitt gleisabschnitt;

  @Override
  public void linkFahrwegelemente(Leitstand leitstand) {
    super.linkFahrwegelemente(leitstand);
    this.weiche = leitstand.getOrCreateWeiche(this.bereich, this.name);
    this.gleisabschnitt = leitstand.getOrCreateGleisabschnitt(this.bereich, this.weiche.getGleisabschnittName());
  }

}

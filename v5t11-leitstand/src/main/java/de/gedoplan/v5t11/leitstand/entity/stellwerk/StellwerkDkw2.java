package de.gedoplan.v5t11.leitstand.entity.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Getter;

/**
 * Stellwerkselement für eine Doppelkreuzungsweiche mit 2 Antrieben.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public class StellwerkDkw2 extends StellwerkElement {

  @Getter
  private Weiche[] weiche;

  @Getter
  private Gleisabschnitt gleisabschnitt;

  @Override
  public void linkFahrwegelemente(Leitstand leitstand) {
    super.linkFahrwegelemente(leitstand);
    this.weiche = new Weiche[] { leitstand.getOrCreateWeiche(this.bereich, this.name + "a"), leitstand.getOrCreateWeiche(this.bereich, this.name + "b") };
    this.gleisabschnitt = leitstand.getOrCreateGleisabschnitt(this.bereich, this.weiche[0].getGleisabschnittName());
  }

}

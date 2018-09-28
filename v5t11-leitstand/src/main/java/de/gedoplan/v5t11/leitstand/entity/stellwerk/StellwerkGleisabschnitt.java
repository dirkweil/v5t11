package de.gedoplan.v5t11.leitstand.entity.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;

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

  @XmlAttribute(name = "signal")
  @Getter
  String signalName;

  @XmlAttribute(name = "signalPos")
  @Getter
  String signalPosition;

  @Getter
  private Gleisabschnitt gleisabschnitt;

  @Getter
  private Signal signal;

  @Override
  public String toString() {
    return super.toString() + " label=" + this.label + ", signalName=" + this.signalName + ", signalPosition=" + this.signalPosition;
  }

  @Override
  public void linkFahrwegelemente(Leitstand leitstand) {
    this.gleisabschnitt = leitstand.getOrCreateGleisabschnitt(this.bereich, this.name);

    if (this.signalName != null) {
      this.signal = leitstand.getOrCreateSignal(this.bereich, this.signalName);
    }

  }

  @Override
  public String getTyp() {
    return "GLEIS";
  }

}

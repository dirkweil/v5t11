package de.gedoplan.v5t11.leitstand.entity.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.Setter;

/**
 * Stellwerkselement.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class StellwerkElement implements Serializable {

  @XmlAttribute
  @Getter
  @Setter
  String bereich;

  @XmlAttribute
  @Getter
  String name;

  @XmlAttribute
  @Getter
  String lage;

  @XmlAttribute(name = "signal")
  @Getter
  String signalName;

  @XmlAttribute(name = "signalPos")
  @Getter
  String signalPosition;

  @Getter
  private Signal signal;

  @XmlAttribute
  @Getter
  @Setter
  int anzahl = 1;

  @Override
  public String toString() {
    return getClass().getSimpleName() + " lage=" + this.lage + ", bereich=" + this.bereich + ", name=" + this.name + ", signalName=" + this.signalName + ", signalPosition=" + this.signalPosition;
  }

  public void linkFahrwegelemente(Leitstand leitstand) {

    if (this.signalName != null) {
      this.signal = leitstand.getOrCreateSignal(this.bereich, this.signalName);
    }
  }

  public boolean isLabel() {
    return false;
  }
}

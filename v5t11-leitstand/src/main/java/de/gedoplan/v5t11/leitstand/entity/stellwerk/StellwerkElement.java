package de.gedoplan.v5t11.leitstand.entity.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;

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

  @XmlAttribute
  @Getter
  @Setter
  int anzahl = 1;

  @Override
  public String toString() {
    return getClass().getSimpleName() + " lage=" + this.lage + ", bereich=" + this.bereich + ", name=" + this.name;
  }

  public abstract void linkFahrwegelemente(Leitstand leitstand);

  // TODO Wird das gebraucht?
  public String getTyp() {
    return null;
  }

  // TODO Wird das gebraucht?
  public String getSignalName() {
    return null;
  }

  // TODO Wird das gebraucht?
  public String getSignalPosition() {
    return null;
  }

  // TODO Wird das gebraucht?
  public boolean isLabel() {
    return false;
  }
}

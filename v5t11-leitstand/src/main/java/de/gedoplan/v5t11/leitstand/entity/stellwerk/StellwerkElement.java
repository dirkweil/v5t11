package de.gedoplan.v5t11.leitstand.entity.stellwerk;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.Setter;

/**
 * Stellwerk.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public class StellwerkElement implements Serializable {
  @XmlAttribute
  @Getter
  String typ = "Leer";

  @XmlAttribute
  @Getter
  String lage;

  @XmlAttribute
  @Getter
  @Setter
  String bereich;

  @XmlAttribute
  @Getter
  String name;

  @XmlAttribute
  @Getter
  boolean label;

  @XmlAttribute(name = "signal")
  @Getter
  String signalName;

  @XmlAttribute(name = "signalPos")
  @Getter
  String signalPosition;

  @XmlAttribute
  @Getter
  @Setter
  int anzahl = 1;

  @Override
  public String toString() {
    return "StellwerkElement [typ=" + this.typ + ", lage=" + this.lage + ", bereich=" + this.bereich + ", name=" + this.name + ", signal=" + this.signalName + ", signalPosition="
        + this.signalPosition + ", anzahl=" + this.anzahl + "]";
  }

}

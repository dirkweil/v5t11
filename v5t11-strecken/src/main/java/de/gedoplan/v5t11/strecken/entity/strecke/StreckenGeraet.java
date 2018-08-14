package de.gedoplan.v5t11.strecken.entity.strecke;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class StreckenGeraet extends Streckenelement {

  /**
   * Schutzfunktion?
   *
   * Elemente mit Schutzfunktion liegen nicht im eigentlichen Fahrweg, sondern schützen ihn nur vor Kollisionen.
   */
  @XmlAttribute
  @Getter
  protected boolean schutz = false;

  @Override
  public String toString() {
    return super.toString() + ", schutz=" + this.schutz;
  }

}

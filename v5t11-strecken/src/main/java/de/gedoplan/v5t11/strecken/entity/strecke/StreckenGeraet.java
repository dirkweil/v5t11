package de.gedoplan.v5t11.strecken.entity.strecke;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class StreckenGeraet extends Streckenelement {

  /**
   * Schutzfunktion?
   *
   * Elemente mit Schutzfunktion liegen nicht im eigentlichen Fahrweg, sondern schützen ihn nur vor Kollisionen.
   */
  @XmlAttribute
  protected boolean schutz = false;

  /**
   * Name der gewünschten Stellung.
   */
  @XmlAttribute(name = "stellung")
  protected String stellungsName;

  @Override
  public String toString() {
    return super.toString() + ", schutz=" + this.schutz + ", stellung=" + this.stellungsName;
  }

}

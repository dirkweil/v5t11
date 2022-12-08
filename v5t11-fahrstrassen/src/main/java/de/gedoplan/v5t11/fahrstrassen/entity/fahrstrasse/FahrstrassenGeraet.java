package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class FahrstrassenGeraet extends Fahrstrassenelement {

  /**
   * Schutzfunktion?
   *
   * Elemente mit Schutzfunktion liegen nicht im eigentlichen Fahrweg, sondern schützen ihn nur vor Kollisionen.
   */
  @XmlAttribute
  @Getter(onMethod_ = @JsonbInclude(full = true))
  protected boolean schutz = false;

//  @Override
//  public String toString() {
//    return super.toString() + ", schutz=" + this.schutz;
//  }

}

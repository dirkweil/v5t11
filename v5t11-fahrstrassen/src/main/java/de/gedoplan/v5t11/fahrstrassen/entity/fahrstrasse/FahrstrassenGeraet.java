package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

import lombok.Getter;

@XmlAccessorType(XmlAccessType.NONE)
public abstract class FahrstrassenGeraet extends Fahrstrassenelement {

  /**
   * Schutzfunktion?
   * <p>
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

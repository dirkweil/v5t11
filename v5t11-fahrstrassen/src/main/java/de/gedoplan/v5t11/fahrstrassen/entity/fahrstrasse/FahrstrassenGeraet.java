package de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse;

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
  @Getter
  protected boolean schutz = false;
}

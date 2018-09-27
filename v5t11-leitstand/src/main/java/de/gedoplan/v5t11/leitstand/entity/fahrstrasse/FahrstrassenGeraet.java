package de.gedoplan.v5t11.leitstand.entity.fahrstrasse;

import lombok.Getter;

public abstract class FahrstrassenGeraet extends Fahrstrassenelement {

  /**
   * Schutzfunktion?
   *
   * Elemente mit Schutzfunktion liegen nicht im eigentlichen Fahrweg, sondern schützen ihn nur vor Kollisionen.
   */
  @Getter
  protected boolean schutz = false;

  @Override
  public String toString() {
    return super.toString() + ", schutz=" + this.schutz;
  }

}

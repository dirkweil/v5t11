package de.gedoplan.v5t11.strecken.entity.fahrweg;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class Geraet extends Fahrwegelement {
  public Geraet(String bereich, String name) {
    super(bereich, name);
  }
}

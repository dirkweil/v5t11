package de.gedoplan.v5t11.strecken.entity.fahrweg;

import de.gedoplan.v5t11.strecken.entity.Bereichselement;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class Fahrwegelement extends Bereichselement {

  public Fahrwegelement(String bereich, String name) {
    super(bereich, name);
  }
}

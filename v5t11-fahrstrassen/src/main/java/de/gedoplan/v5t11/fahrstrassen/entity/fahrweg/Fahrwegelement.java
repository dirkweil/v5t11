package de.gedoplan.v5t11.fahrstrassen.entity.fahrweg;

import de.gedoplan.v5t11.fahrstrassen.entity.Bereichselement;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class Fahrwegelement extends Bereichselement {

  public Fahrwegelement(String bereich, String name) {
    super(bereich, name);
  }
}

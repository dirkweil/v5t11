package de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug;

import jakarta.persistence.EnumeratedValue;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum FahrzeugTyp {
  LOK("Lok", "L"), WAGEN("Wagen", "W"), SONSTIGES("Sonstiges", "S");

  @Getter
  private String bezeichnung;

  @EnumeratedValue
  private final String shortCode;
}

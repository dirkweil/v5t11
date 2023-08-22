package de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug;

import de.gedoplan.baselibs.persistence.entity.ToStringable;

import jakarta.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ConfigDefinition extends ToStringable {
  private int key;
  private String beschreibung;
}

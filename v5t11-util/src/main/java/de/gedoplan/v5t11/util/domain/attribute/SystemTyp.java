package de.gedoplan.v5t11.util.domain.attribute;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum SystemTyp {
  SX1(31, "Par"),
  SX2(127, "Par"),
  DCC(126, "CV");

  private int maxFahrstufe;
  private String konfigWertBezeichnung;

}

package de.gedoplan.v5t11.util.domain.attribute;

import lombok.Getter;

public enum SystemTyp {
  SX1(31),
  SX2(127),
  DCC(126);

  @Getter
  private int maxFahrstufe;

  private SystemTyp(int maxFahrstufe) {
    this.maxFahrstufe = maxFahrstufe;
  }
}

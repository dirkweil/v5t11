package de.gedoplan.v5t11.util.domain.entity;

import lombok.Getter;

public enum SystemTyp {
  SX1(31),
  SX2(127),
  DCC_K_14(14),
  DCC_K_28(28),
  DCC_K_126(126),
  DCC_L_14(14),
  DCC_L_28(28),
  DCC_L_126(126);

  @Getter
  private int maxFahrstufe;

  private SystemTyp(int maxFahrstufe) {
    this.maxFahrstufe = maxFahrstufe;

  }
}

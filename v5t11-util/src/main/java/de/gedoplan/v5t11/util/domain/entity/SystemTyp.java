package de.gedoplan.v5t11.util.domain.entity;

import lombok.Getter;

public enum SystemTyp {
  SX1(31, false),
  SX2(127, false),
  DCC_K_14(14, true),
  DCC_K_28(28, true),
  DCC_K_126(126, true),
  DCC_L_14(14, true),
  DCC_L_28(28, true),
  DCC_L_126(126, true);

  @Getter
  private int maxFahrstufe;

  @Getter
  private boolean dcc;

  private SystemTyp(int maxFahrstufe, boolean dcc) {
    this.maxFahrstufe = maxFahrstufe;
    this.dcc = dcc;
  }
}

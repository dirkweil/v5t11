package de.gedoplan.v5t11.util.domain.entity;

import lombok.Getter;

public enum SystemTyp {
  SX1(0x00, 31),
  SX2(0x04, 127),
  DCC(0x07, 126);

  @Getter
  private int formatCode;

  @Getter
  private int maxFahrstufe;

  private SystemTyp(int formatCode, int maxFahrstufe) {
    this.formatCode = formatCode;
    this.maxFahrstufe = maxFahrstufe;

  }

  public static SystemTyp valueOf(int formatCode) {
    for (SystemTyp systemTyp : SystemTyp.values()) {
      if (systemTyp.formatCode == formatCode) {
        return systemTyp;
      }
    }
    throw new IllegalArgumentException(String.format("Ungültiger Formatcode: 0x%02x", formatCode));
  }

}

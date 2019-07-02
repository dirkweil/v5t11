package de.gedoplan.v5t11.status.entity;

import lombok.Getter;

public enum SystemTyp {
  SX1(0x00),
  SX2(0x04),
  DCC(0x07);

  @Getter
  private int formatCode;

  private SystemTyp(int formatCode) {
    this.formatCode = formatCode;

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

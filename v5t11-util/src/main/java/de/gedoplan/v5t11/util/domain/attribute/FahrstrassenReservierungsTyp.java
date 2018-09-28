package de.gedoplan.v5t11.util.domain.attribute;

public enum FahrstrassenReservierungsTyp {
  UNRESERVIERT, ZUGFAHRT, RANGIERFAHRT;

  public static FahrstrassenReservierungsTyp valueOfLenient(String s) {
    try {
      return FahrstrassenReservierungsTyp.valueOf(s);
    } catch (IllegalArgumentException e) {
      for (FahrstrassenReservierungsTyp value : values()) {
        if (value.name().startsWith(s)) {
          return value;
        }
      }

      throw e;
    }
  }
}

package de.gedoplan.v5t11.util.domain.attribute;

public enum FahrstrassenFilter {
  FREI, RESERVIERT, NON_COMBI;

  public static FahrstrassenFilter valueOfLenient(String s) {
    try {
      return FahrstrassenFilter.valueOf(s);
    } catch (IllegalArgumentException e) {
      for (FahrstrassenFilter value : values()) {
        if (value.name().startsWith(s)) {
          return value;
        }
      }

      throw e;
    }
  }
}

package de.gedoplan.v5t11.util.domain.attribute;

/**
 * Weichenstellung.
 *
 * @author dw
 */
public enum WeichenStellung {
  GERADE, ABZWEIGEND;

  public static WeichenStellung valueOfLenient(String s) {
    try {
      return WeichenStellung.valueOf(s);
    } catch (IllegalArgumentException e) {
      for (WeichenStellung value : values()) {
        if (value.name().startsWith(s)) {
          return value;
        }
      }

      throw e;
    }
  }

}

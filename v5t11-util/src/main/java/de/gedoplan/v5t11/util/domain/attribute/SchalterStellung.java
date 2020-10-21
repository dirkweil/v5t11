package de.gedoplan.v5t11.util.domain.attribute;

/**
 * Schalterstellung.
 *
 * @author dw
 */
public enum SchalterStellung {
  // Achtung: Die Reihenfolge der beiden Konstanten muss erhalten bleiben, da die Ordinalwerte als Stellungswerte genutzt werden!
  AUS, EIN;

  public static SchalterStellung valueOfLenient(String s) {
    try {
      return SchalterStellung.valueOf(s);
    } catch (IllegalArgumentException e) {
      for (SchalterStellung value : values()) {
        if (value.name().startsWith(s)) {
          return value;
        }
      }

      throw e;
    }
  }

}

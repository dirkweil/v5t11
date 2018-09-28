package de.gedoplan.v5t11.util.domain.attribute;

/**
 * Signalstellung.
 *
 * @author dw
 */
public enum SignalStellung {
  /**
   * Halt (Hp0, Hp00, Sr0, Vr0).
   */
  HALT,

  /**
   * Fahrt (Hp1, Vr1).
   */
  FAHRT,

  /**
   * Langsamfahrt (Hp2, Vr2).
   */
  LANGSAMFAHRT,

  /**
   * Rangierfahrt (Sr1).
   */
  RANGIERFAHRT,

  /**
   * Dunkel (Vorsignal am Hauptsignalmast bei Hp0).
   */
  DUNKEL;

  public static SignalStellung valueOfLenient(String s) {
    try {
      return SignalStellung.valueOf(s);
    } catch (IllegalArgumentException e) {
      for (SignalStellung value : values()) {
        if (value.name().startsWith(s)) {
          return value;
        }
      }

      throw e;
    }
  }

};

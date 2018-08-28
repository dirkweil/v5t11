package de.gedoplan.v5t11.util.domain;

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
  DUNKEL

};

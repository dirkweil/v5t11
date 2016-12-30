package de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.lokdecoder;

/**
 * Basisklasse für Lokdecoder von Doehler & Haass.
 *
 * @author dw
 */
public abstract class DHLokdecoder extends SxLokdecoder {
  @Override
  public Class<?> getProgrammierklasse() {
    return DHLokdecoder.class;
  }
}

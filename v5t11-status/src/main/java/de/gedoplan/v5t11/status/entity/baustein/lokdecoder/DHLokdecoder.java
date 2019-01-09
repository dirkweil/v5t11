package de.gedoplan.v5t11.status.entity.baustein.lokdecoder;

/**
 * Basisklasse für Lokdecoder von Doehler & Haass.
 *
 * @author dw
 */
public abstract class DHLokdecoder extends SxLokdecoder {
  protected DHLokdecoder(int byteAnzahl) {
    super(byteAnzahl);
  }
}

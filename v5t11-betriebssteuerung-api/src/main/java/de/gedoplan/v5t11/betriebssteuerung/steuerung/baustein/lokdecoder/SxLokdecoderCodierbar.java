package de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.lokdecoder;

/**
 * Basisklasse für Selectrix-Lokdecoder.
 *
 * @author dw
 */
public abstract class SxLokdecoderCodierbar extends SxLokdecoder {
  @Override
  public Class<?> getProgrammierklasse() {
    return null;
  }
}

package de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.lokdecoder;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Lokdecoder;

/**
 * Basisklasse für Selectrix-Lokdecoder.
 *
 * @author dw
 */
public abstract class SxLokdecoder extends Lokdecoder {

  @Override
  public String getProgrammierfamilie() {
    return SxLokdecoder.class.getSimpleName();
  }
}

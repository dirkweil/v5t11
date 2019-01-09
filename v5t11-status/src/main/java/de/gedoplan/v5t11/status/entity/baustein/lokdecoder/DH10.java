package de.gedoplan.v5t11.status.entity.baustein.lokdecoder;

import de.gedoplan.v5t11.status.entity.baustein.Konfigurierbar;

/**
 * Lokdecoder DH10C.
 *
 * Kleiner Lokdecoder mit Zusatzparametern und Zusatzadresse.
 *
 * @author dw
 */
@Konfigurierbar(programmierFamilie = DHLokdecoder.class)
public class DH10 extends DHLokdecoder {
  public DH10() {
    super(1);
  }
}

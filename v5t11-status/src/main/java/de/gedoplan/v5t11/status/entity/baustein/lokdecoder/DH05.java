package de.gedoplan.v5t11.status.entity.baustein.lokdecoder;

import de.gedoplan.v5t11.status.entity.baustein.Konfigurierbar;

/**
 * Lokdecoder DH05A.
 *
 * Sehr kleiner Lokdecoder mit Zusatzparametern und Zusatzadresse.
 *
 * @author dw
 */
@Konfigurierbar(programmierFamilie = "DHLokDecoder")
public class DH05 extends DHLokdecoder {
  public DH05() {
    super(1);
  }
}

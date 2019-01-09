package de.gedoplan.v5t11.status.entity.baustein.lokdecoder;

import de.gedoplan.v5t11.status.entity.baustein.Konfigurierbar;

/**
 * Lokdecoder DHL100.
 *
 * Kleiner Lokdecoder mit Zusatzparametern.
 *
 * @author dw
 */
@Konfigurierbar(programmierFamilie = "DHLokDecoder")
public class DHL100 extends DHLokdecoder {
  public DHL100() {
    super(1);
  }
}

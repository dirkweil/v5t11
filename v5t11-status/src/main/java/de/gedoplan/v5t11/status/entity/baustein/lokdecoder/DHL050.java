package de.gedoplan.v5t11.status.entity.baustein.lokdecoder;

import de.gedoplan.v5t11.status.entity.baustein.Konfigurierbar;

/**
 * Lokdecoder DHL100.
 *
 * Sehr kleiner Lokdecoder mit Zusatzparametern.
 *
 * @author dw
 */
@Konfigurierbar(programmierFamilie = DHLokdecoder.class)
public class DHL050 extends DHLokdecoder {
  public DHL050() {
    super(1);
  }
}

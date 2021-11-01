package de.gedoplan.v5t11.status.entity.baustein.funktionsdecoder;

import de.gedoplan.v5t11.status.entity.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.status.entity.baustein.Konfigurierbar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Funktionsdecoder.
 *
 * Objekte dieser Klasse repräsentieren Decoder vom Typ WD-Miba 3
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@Konfigurierbar
public class WDMiba3 extends Funktionsdecoder {
  public WDMiba3() {
    super(1);
  }
}

package de.gedoplan.v5t11.status.entity.baustein.funktionsdecoder;

import de.gedoplan.v5t11.status.entity.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.status.entity.baustein.Konfigurierbar;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

/**
 * Funktionsdecoder.
 * <p>
 * Objekte dieser Klasse repräsentieren Decoder vom Typ WD-Miba
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@Konfigurierbar
public class WDMiba extends Funktionsdecoder {
  public WDMiba() {
    super(1);
  }
}

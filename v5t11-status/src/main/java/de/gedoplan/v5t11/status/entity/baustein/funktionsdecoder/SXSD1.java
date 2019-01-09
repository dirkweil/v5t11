package de.gedoplan.v5t11.status.entity.baustein.funktionsdecoder;

import de.gedoplan.v5t11.status.entity.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.status.entity.baustein.Konfigurierbar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Funktionsdecoder.
 *
 * Objekte dieser Klasse repräsentieren Encoder vom Typ SX-FD-1.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@Konfigurierbar
public class SXSD1 extends Funktionsdecoder {
  protected SXSD1() {
    super(2);
  }
}

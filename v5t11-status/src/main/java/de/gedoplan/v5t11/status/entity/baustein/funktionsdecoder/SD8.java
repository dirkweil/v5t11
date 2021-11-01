package de.gedoplan.v5t11.status.entity.baustein.funktionsdecoder;

import de.gedoplan.v5t11.status.entity.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.status.entity.baustein.Konfigurierbar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Funktionsdecoder.
 *
 * Objekte dieser Klasse repräsentieren Decoder vom Typ SD-8. Dies sind Servodecoder von Norbert Martsch zum Anschluss von 8
 * Servos für Weichen o. ä.
 *
 * @author dw
 */
@Konfigurierbar
@XmlAccessorType(XmlAccessType.NONE)
public class SD8 extends Funktionsdecoder {
  public SD8() {
    super(1);
  }
}

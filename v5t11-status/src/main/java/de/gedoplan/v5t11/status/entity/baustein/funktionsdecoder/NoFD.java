package de.gedoplan.v5t11.status.entity.baustein.funktionsdecoder;

import de.gedoplan.v5t11.status.entity.baustein.Funktionsdecoder;

import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Funktionsdecoder.
 *
 * Objekte dieser Klasse repräsentieren Decoder vom Typ WD-Miba
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public class NoFD extends Funktionsdecoder {
  protected NoFD() {
    super(0);
    this.id = UUID.randomUUID().toString();
  }
}

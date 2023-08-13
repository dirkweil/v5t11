package de.gedoplan.v5t11.status.entity.baustein.funktionsdecoder;

import de.gedoplan.v5t11.status.entity.baustein.Funktionsdecoder;

import java.util.UUID;

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
public class NoFD extends Funktionsdecoder {
  public NoFD() {
    super(0);
    this.id = UUID.randomUUID().toString();
  }
}

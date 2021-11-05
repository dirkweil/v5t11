package de.gedoplan.v5t11.status.entity.baustein.besetztmelder;

import de.gedoplan.v5t11.status.entity.baustein.Besetztmelder;
import de.gedoplan.v5t11.status.entity.baustein.Konfigurierbar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Besetztmelder des Typs SX-BM-1.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@Konfigurierbar
public class SXBM1 extends Besetztmelder {
  public SXBM1() {
    super(1);
  }
}

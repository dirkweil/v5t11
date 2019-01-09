package de.gedoplan.v5t11.status.entity.baustein.besetztmelder;

import de.gedoplan.v5t11.status.entity.baustein.Besetztmelder;
import de.gedoplan.v5t11.status.entity.baustein.Konfigurierbar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Besetztmelder des Typs BM Miba 3.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@Konfigurierbar
public class BMMiba3 extends Besetztmelder {
  protected BMMiba3() {
    super(1);
  }
}

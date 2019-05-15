package de.gedoplan.v5t11.status.entity.baustein.besetztmelder;

import de.gedoplan.v5t11.status.entity.baustein.Besetztmelder;
import de.gedoplan.v5t11.status.entity.baustein.Konfigurierbar;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Besetztmelder des Typs Viessmann 5262.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@Konfigurierbar
public class VM5262 extends Besetztmelder {
  protected VM5262() {
    super(1);
  }
}

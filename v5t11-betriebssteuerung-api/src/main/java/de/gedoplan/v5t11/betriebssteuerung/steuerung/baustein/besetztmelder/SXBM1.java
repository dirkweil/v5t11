package de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.besetztmelder;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Besetztmelder;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Besetztmelder des Typs SX-BM-1.
 * 
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@Typed
public class SXBM1 extends Besetztmelder
{
  /**
   * Konstruktor für interne Zwecke.
   */
  protected SXBM1()
  {
    super(1);
  }

  @Produces
  public static SXBM1 createNewInstance()
  {
    return new SXBM1();
  }
}

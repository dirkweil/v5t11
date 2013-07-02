package de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.besetztmelder;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Besetztmelder;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Besetztmelder des Typs BM Miba 3.
 * 
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@Typed
public class BMMiba3 extends Besetztmelder
{
  /**
   * Konstruktor für interne Zwecke.
   */
  protected BMMiba3()
  {
    super(1);
  }

  @Produces
  public static BMMiba3 createNewInstance()
  {
    return new BMMiba3();
  }

}

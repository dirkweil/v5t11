package de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.funktionsdecoder;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Funktionsdecoder;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
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
@Typed
public class SXSD1 extends Funktionsdecoder
{
  /**
   * Konstruktor.
   */
  protected SXSD1()
  {
    super(2);
  }

  @Produces
  public static SXSD1 createNewInstance()
  {
    return new SXSD1();
  }
}

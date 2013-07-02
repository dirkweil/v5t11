package de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.funktionsdecoder;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Funktionsdecoder;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Funktionsdecoder.
 * 
 * Objekte dieser Klasse repräsentieren Decoder vom Typ STR-FD-1.
 * 
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@Typed
public class STRFD1 extends Funktionsdecoder
{
  /**
   * Konstruktor.
   */
  protected STRFD1()
  {
    super(1);
  }

  @Produces
  public static STRFD1 createNewInstance()
  {
    return new STRFD1();
  }
}

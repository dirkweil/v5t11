package de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.funktionsdecoder;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Funktionsdecoder;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
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
@Typed
public class WDMiba extends Funktionsdecoder
{
  /**
   * Konstruktor.
   */
  protected WDMiba()
  {
    super(1);
  }

  @Produces
  public static WDMiba createNewInstance()
  {
    return new WDMiba();
  }
}

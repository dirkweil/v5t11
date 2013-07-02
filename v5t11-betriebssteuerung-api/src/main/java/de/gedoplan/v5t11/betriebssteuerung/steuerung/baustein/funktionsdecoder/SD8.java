package de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.funktionsdecoder;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Funktionsdecoder;

import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Typed;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Funktionsdecoder.
 * 
 * Objekte dieser Klasse repräsentieren Decoder vom Typ SD-8. Dies sind Servodecoder von Norbert Martsch zum Anschluss von 8
 * Servos für Weichen o. ä.
 * 
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@Typed
public class SD8 extends Funktionsdecoder
{
  /**
   * Konstruktor.
   */
  protected SD8()
  {
    super(1);
  }

  @Produces
  public static SD8 createNewInstance()
  {
    return new SD8();
  }
}

/*
 * Created on 11.04.2006 by dw
 */
package de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Hauptsignal, 2-begriffig (Halt, Fahrt).
 * 
 * Das Signal belegt am Decoder SXSD1 einen Ausgang und wird wie folgt angeschlossen: Pin1 = +5V, Pin3 = rot, Pin4 = grün
 */
@XmlAccessorType(XmlAccessType.NONE)
public class HauptsignalRtGn extends Hauptsignal
{
  private static final Color[] FARBEN_HALT  = new Color[] { null, Color.red };
  private static final Color[] FARBEN_FAHRT = new Color[] { Color.green, null };
  private static final Color[] FARBEN_NULL  = new Color[] { null, null };

  /**
   * Konstruktor.
   */
  protected HauptsignalRtGn()
  {
    super(1);
    addErlaubteStellung(Stellung.HALT, 0);
    addErlaubteStellung(Stellung.FAHRT, 1);
  }

  @Override
  public Color[] getGBSFarben()
  {
    switch (getStellung())
    {
    case HALT:
      return FARBEN_HALT;

    case FAHRT:
      return FARBEN_FAHRT;

    default:
      return FARBEN_NULL;
    }
  }
}

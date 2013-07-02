/*
 * Created on 11.04.2006 by dw
 */
package de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Hauptsignal, 2-begriffig (Halt, Langsamfahrt).
 * 
 * Das Signal belegt am Decoder SXSD1 einen Ausgang und wird wie folgt angeschlossen: Pin1 = +5V, Pin3 = rot, Pin4 = grün + gelb
 */
@XmlAccessorType(XmlAccessType.NONE)
public class HauptsignalRtGe extends Hauptsignal
{
  private static final Color[] FARBEN_HALT         = new Color[] { null, Color.red };
  private static final Color[] FARBEN_LANGSAMFAHRT = new Color[] { Color.green, Color.yellow };
  private static final Color[] FARBEN_NULL         = new Color[] { null, null };

  /**
   * Konstruktor.
   */
  protected HauptsignalRtGe()
  {
    super(1);
    addErlaubteStellung(Stellung.HALT, 0);
    addErlaubteStellung(Stellung.LANGSAMFAHRT, 1);
  }

  @Override
  public Color[] getGBSFarben()
  {
    switch (getStellung())
    {
    case HALT:
      return FARBEN_HALT;

    case LANGSAMFAHRT:
      return FARBEN_LANGSAMFAHRT;

    default:
      return FARBEN_NULL;
    }
  }

}

/*
 * Created on 11.04.2006 by dw
 */
package de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Hauptsignal, 3-begriffig (Halt, Fahrt, Langsamfahrt).
 * 
 * Das Signal belegt am Decoder SXSD1 zwei Ausgänge (Idx, Idx+1) und wird wie folgt angeschlossen:
 * <ul>
 * <li>Ausgang Idx: Pin1 = +5V, Pin3 = rot, Pin4 = grün</li>
 * <li>Ausgang Idx+1: Pin4 = gelb</li>
 * </ul>
 */
@XmlAccessorType(XmlAccessType.NONE)
public class HauptsignalRtGnGe extends Hauptsignal
{
  private static final Color[] FARBEN_HALT         = new Color[] { null, Color.red };
  private static final Color[] FARBEN_FAHRT        = new Color[] { Color.green, null };
  private static final Color[] FARBEN_LANGSAMFAHRT = new Color[] { Color.green, Color.yellow };
  private static final Color[] FARBEN_NULL         = new Color[] { null, null };

  /**
   * Konstruktor.
   */
  protected HauptsignalRtGnGe()
  {
    super(2);
    addErlaubteStellung(Stellung.HALT, 0);
    addErlaubteStellung(Stellung.FAHRT, 1);
    addErlaubteStellung(Stellung.LANGSAMFAHRT, 3);
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

    case LANGSAMFAHRT:
      return FARBEN_LANGSAMFAHRT;

    default:
      return FARBEN_NULL;
    }
  }
}

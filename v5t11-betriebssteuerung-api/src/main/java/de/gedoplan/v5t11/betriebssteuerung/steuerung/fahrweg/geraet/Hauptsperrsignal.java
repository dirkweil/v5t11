/*
 * Created on 11.04.2006 by dw
 */
package de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class Hauptsperrsignal extends Hauptsignal
{
  private static final Color[] FARBEN_HALT         = new Color[] { null, Color.red };
  private static final Color[] FARBEN_FAHRT        = new Color[] { Color.green, null };
  private static final Color[] FARBEN_LANGSAMFAHRT = new Color[] { Color.green, Color.yellow };
  private static final Color[] FARBEN_RANGIERFAHRT = new Color[] { Color.red, Color.white };
  private static final Color[] FARBEN_NULL         = new Color[] { null, null };

  /**
   * Konstruktor.
   */
  protected Hauptsperrsignal()
  {
    super(2);
    addErlaubteStellung(Stellung.HALT, 0);
    addErlaubteStellung(Stellung.FAHRT, 1);
    addErlaubteStellung(Stellung.LANGSAMFAHRT, 3);
    addErlaubteStellung(Stellung.RANGIERFAHRT, 2);
  }

  @Override
  public Color[] getGBSFarben()
  {
    switch (getStellung())
    {
    case HALT:
      return Hauptsperrsignal.FARBEN_HALT;

    case FAHRT:
      return Hauptsperrsignal.FARBEN_FAHRT;

    case LANGSAMFAHRT:
      return Hauptsperrsignal.FARBEN_LANGSAMFAHRT;

    case RANGIERFAHRT:
      return Hauptsperrsignal.FARBEN_RANGIERFAHRT;

    default:
      return Hauptsperrsignal.FARBEN_NULL;
    }
  }
}

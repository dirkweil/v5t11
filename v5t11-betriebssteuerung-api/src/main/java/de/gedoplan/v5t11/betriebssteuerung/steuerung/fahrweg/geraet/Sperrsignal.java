/*
 * Created on 11.04.2006 by dw
 */
package de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class Sperrsignal extends Signal
{
  private static final Color[] FARBEN_HALT         = new Color[] { Color.red };
  private static final Color[] FARBEN_RANGIERFAHRT = new Color[] { Color.white };
  private static final Color[] FARBEN_NULL         = new Color[] { null };

  /**
   * Konstruktor.
   */
  protected Sperrsignal()
  {
    super(1);
    addErlaubteStellung(Stellung.HALT, 0);
    addErlaubteStellung(Stellung.RANGIERFAHRT, 1);
  }

  @Override
  public Color[] getGBSFarben()
  {
    switch (getStellung())
    {
    case HALT:
      return FARBEN_HALT;

    case RANGIERFAHRT:
      return FARBEN_RANGIERFAHRT;

    default:
      return FARBEN_NULL;
    }
  }
}

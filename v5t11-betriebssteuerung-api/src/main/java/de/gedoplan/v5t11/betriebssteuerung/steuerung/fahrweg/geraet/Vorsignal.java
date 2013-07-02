/*
 * Created on 11.04.2006 by dw
 */
package de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class Vorsignal extends Signal
{
  private static final Color[] FARBEN_HALT         = new Color[] { null, Color.yellow };
  private static final Color[] FARBEN_FAHRT        = new Color[] { Color.green, null };
  private static final Color[] FARBEN_LANGSAMFAHRT = new Color[] { Color.green, Color.yellow };
  private static final Color[] FARBEN_NULL         = new Color[] { null, null };

  /**
   * Konstruktor.
   */
  protected Vorsignal()
  {
    super(2);
    addErlaubteStellung(Stellung.HALT, 0);
    addErlaubteStellung(Stellung.FAHRT, 3);
    addErlaubteStellung(Stellung.LANGSAMFAHRT, 2);
    addErlaubteStellung(Stellung.DUNKEL, 1);
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

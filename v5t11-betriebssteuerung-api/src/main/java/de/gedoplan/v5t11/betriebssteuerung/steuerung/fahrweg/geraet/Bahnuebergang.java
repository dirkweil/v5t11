/*
 * Created on 11.04.2006 by dw
 */
package de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet;

import java.awt.Color;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class Bahnuebergang extends Signal
{
  private static final Color[] FARBEN_HALT  = new Color[] { Color.red };
  private static final Color[] FARBEN_FAHRT = new Color[] { Color.white };
  private static final Color[] FARBEN_NULL  = new Color[] { null };

  /**
   * Konstruktor.
   */
  protected Bahnuebergang()
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

  /**
   * Bahnübergang schliessen.
   * 
   * Conveniance-Methode äquivalent zu {@link #setStellung(Stellung) setStellung(FAHRT)}.
   */
  public void schliessen()
  {
    setStellung(Stellung.FAHRT);
  }

  /**
   * Bahnübergang öffnen.
   * 
   * Conveniance-Methode äquivalent zu {@link #setStellung(Stellung) setStellung(HALT)}.
   */
  public void oeffnen()
  {
    setStellung(Stellung.HALT);
  }
}

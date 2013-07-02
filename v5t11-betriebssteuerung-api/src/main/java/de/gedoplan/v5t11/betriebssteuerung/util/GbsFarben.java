package de.gedoplan.v5t11.betriebssteuerung.util;

import java.awt.Color;

// CHECKSTYLE:OFF

/**
 * Sammlung aller verwendeter Farben im GBS.
 * 
 * @author dw
 */
public interface GbsFarben
{
  public static final Color GLEIS_FREI                           = Color.BLACK;
  public static final Color GLEIS_BESETZT                        = Color.RED;
  public static final Color GLEIS_IN_ZUGFAHRSTRASSE              = Color.YELLOW;
  public static final Color GLEIS_IN_RANGIERFAHRSTRASSE          = Color.WHITE;
  public static final Color GLEIS_IN_VORGESCHLAGENER_FAHRSTRASSE = Color.BLUE;
  public static final Color GLEIS_GESPERRT                       = new Color(150, 150, 150);

}

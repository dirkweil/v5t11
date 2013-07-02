package de.gedoplan.v5t11.betriebssteuerung.listener;

/**
 * Event für Wertänderung.
 * 
 * @author dw
 */
public class ValueChangedEvent extends Event
{
  /**
   * Konstruktor.
   * 
   * @param source Event-Quelle
   */
  public ValueChangedEvent(Object source)
  {
    super(source);
  }
}

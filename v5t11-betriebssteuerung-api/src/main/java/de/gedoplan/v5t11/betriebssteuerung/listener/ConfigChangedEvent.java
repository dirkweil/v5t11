package de.gedoplan.v5t11.betriebssteuerung.listener;

/**
 * Event für Konfigurationsänderung.
 * 
 * @author dw
 */
public class ConfigChangedEvent extends Event
{
  /**
   * Konstruktor.
   * 
   * @param source Event-Quelle
   */
  public ConfigChangedEvent(Object source)
  {
    super(source);
  }
}

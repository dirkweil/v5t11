package de.gedoplan.v5t11.betriebssteuerung.listener;

/**
 * Listener für {@link ConfigChangedEvent}.
 * 
 * @author dw
 */
public interface ConfigChangedListener extends EventListener<ConfigChangedEvent>
{
  /**
   * Wert der Event Source hat sich verändert.
   * 
   * @param event Event
   */
  public void configChanged(ConfigChangedEvent event);
}

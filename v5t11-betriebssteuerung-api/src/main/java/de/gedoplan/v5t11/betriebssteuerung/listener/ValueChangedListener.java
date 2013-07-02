package de.gedoplan.v5t11.betriebssteuerung.listener;

/**
 * Listener für {@link ValueChangedEvent}.
 * 
 * @author dw
 */
public interface ValueChangedListener extends EventListener<ValueChangedEvent>
{
  /**
   * Wert der Event Source hat sich verändert.
   * 
   * @param event Event
   */
  public void valueChanged(ValueChangedEvent event);
}

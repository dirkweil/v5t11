package de.gedoplan.v5t11.betriebssteuerung.listener;


/**
 * Registry für {@link ValueChangedListener}.
 * 
 * @author dw
 */
public class ValueChangedListenerRegistry extends EventListenerRegistry<ValueChangedEvent, ValueChangedListener>
{
  @Override
  protected void sendEvent(ValueChangedListener listener, ValueChangedEvent event)
  {
    listener.valueChanged(event);
  }
}

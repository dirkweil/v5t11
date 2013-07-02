package de.gedoplan.v5t11.betriebssteuerung.listener;

/**
 * Registry für {@link ConfigChangedListener}.
 * 
 * @author dw
 */
public class ConfigChangedListenerRegistry extends EventListenerRegistry<ConfigChangedEvent, ConfigChangedListener>
{
  @Override
  protected void sendEvent(ConfigChangedListener listener, ConfigChangedEvent event)
  {
    listener.configChanged(event);
  }
}

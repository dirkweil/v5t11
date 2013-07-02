package de.gedoplan.v5t11.betriebssteuerung.listener;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Registry für {@link ValueChangedListener}.
 * 
 * @author dw
 */
public abstract class EventListenerRegistry<E extends Event, L extends EventListener<E>> implements Serializable
{
  private List<L>          listeners = new CopyOnWriteArrayList<>();

  private static final Log LOGGER    = LogFactory.getLog(EventListenerRegistry.class);

  /**
   * Listener hinzufügen.
   * 
   * @param listener Listener
   */
  public void addListener(L listener)
  {
    this.listeners.add(listener);
  }

  /**
   * Listener entfernen.
   * 
   * @param listener Listener
   */
  public void removeListener(L listener)
  {
    this.listeners.remove(listener);
  }

  /**
   * Event versenden.
   * 
   * @param event Event
   */
  public void sendEvent(E event)
  {
    if (LOGGER.isTraceEnabled())
    {
      LOGGER.trace(event);
    }

    for (L listener : this.listeners)
    {
      sendEvent(listener, event);
    }
  }

  protected abstract void sendEvent(L listener, E event);

}

package de.gedoplan.v5t11.betriebssteuerung.event;

import java.lang.annotation.Annotation;

import javax.enterprise.inject.spi.BeanManager;

public abstract class EventFirer
{
  private static BeanManager beanManager;

  /**
   * Wert setzen: {@link #beanManager}.
   * 
   * @param beanManager Wert
   */
  public static void setBeanManager(BeanManager beanManager)
  {
    EventFirer.beanManager = beanManager;
  }

  public static void fireEvent(Object event, Annotation... qualifier)
  {
    if (beanManager != null)
    {
      beanManager.fireEvent(event, qualifier);
    }
  }

}

package de.gedoplan.v5t11.status.util;

import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;

public final class EventFirer {
  private static BeanManager beanManager = null;

  public static void fire(Object event) {
    if (beanManager == null) {
      beanManager = CDI.current().select(BeanManager.class).get();
    }

    beanManager.fireEvent(event);
  }

  private EventFirer() {
  }
}

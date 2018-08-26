package de.gedoplan.v5t11.util.cdi;

import java.lang.annotation.Annotation;

import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.TypeLiteral;

public final class EventFirer {
  private static Event<Object> eventSource = null;

  public static void fire(Object event, Annotation... qualifiers) {
    if (eventSource == null) {
      eventSource = CDI.current()
          .select(new TypeLiteral<Event<Object>>() {})
          .get();
    }

    Event<Object> es = eventSource;
    if (qualifiers.length != 0) {
      es = es.select(qualifiers);
    }
    es.fire(event);
    es.fireAsync(event);
  }

  private EventFirer() {
  }
}

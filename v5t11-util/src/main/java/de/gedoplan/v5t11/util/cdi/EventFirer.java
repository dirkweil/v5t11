package de.gedoplan.v5t11.util.cdi;

import java.lang.annotation.Annotation;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Event;
import jakarta.enterprise.inject.Any;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;

/**
 * Helferklasse zum Feuern von CDI-Events.
 * <p>
 * Die übergebenen Events werden synchron und asynchron gefeuert.
 *
 * @author dw
 */
@ApplicationScoped
public class EventFirer {

  @Inject
  @Any
  Event<Object> eventSource;

  public void fire(Object event, Annotation... qualifiers) {
    Event<Object> es = this.eventSource;
    if (qualifiers.length != 0) {
      es = es.select(qualifiers);
    } else {
      es = es.select(Default.Literal.INSTANCE);
    }
    es.fire(event);
    es.fireAsync(event);
  }
}

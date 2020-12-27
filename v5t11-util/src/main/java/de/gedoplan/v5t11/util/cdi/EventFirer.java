package de.gedoplan.v5t11.util.cdi;

import java.lang.annotation.Annotation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

/**
 * Helferklasse zum Feuern von CDI-Events.
 *
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

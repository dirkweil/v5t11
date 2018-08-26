package de.gedoplan.v5t11.util.cdi;

import java.lang.annotation.Annotation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Event;
import javax.enterprise.inject.spi.CDI;
import javax.inject.Inject;

/**
 * Helferklasse zum Feuern von CDI-Events.
 *
 * Die übergebenen Events werden synchron und asynchron gefeuert.
 *
 * EventFirer kann als Dependency genutzt werden (mittels @Inject). Alternativ kann eine Instanz von EventFirer
 * auch mittels {@link #getInstance()} erstellt werden.
 *
 * @author dw
 */
@ApplicationScoped
public class EventFirer {

  private static EventFirer INSTANCE = null;

  public static EventFirer getInstance() {
    if (INSTANCE == null) {
      INSTANCE = CDI.current().select(EventFirer.class).get();
    }
    return INSTANCE;
  }

  @Inject
  private Event<Object> eventSource;

  public void fire(Object event, Annotation... qualifiers) {
    Event<Object> es = this.eventSource;
    if (qualifiers.length != 0) {
      es = es.select(qualifiers);
    }
    es.fire(event);
    es.fireAsync(event);
  }
}

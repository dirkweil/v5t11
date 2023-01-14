package de.gedoplan.v5t11.leitstand.service;

import de.gedoplan.v5t11.leitstand.messaging.OutgoingHandler;
import de.gedoplan.v5t11.util.jsf.NavigationItem;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Default;
import javax.inject.Inject;

@ApplicationScoped
public class EventDispatcher {

  @Inject
  Logger log;

  @Inject
  OutgoingHandler outgoingHandler;

  void dispatch(@Observes @Default NavigationItem navigationItem) {
    this.outgoingHandler.publish(navigationItem);
  }
}

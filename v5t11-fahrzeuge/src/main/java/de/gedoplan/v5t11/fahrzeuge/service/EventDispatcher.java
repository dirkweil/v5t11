package de.gedoplan.v5t11.fahrzeuge.service;

import de.gedoplan.v5t11.fahrzeuge.messaging.OutgoingHandler;
import de.gedoplan.v5t11.util.jsf.NavigationItem;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.logging.Logger;

@ApplicationScoped
public class EventDispatcher {

  @Inject
  Logger log;

  @Inject
  OutgoingHandler outgoingHandler;

  void dispatch(@Observes NavigationItem navigationItem) {
    this.outgoingHandler.publish(navigationItem);
  }
}

package de.gedoplan.v5t11.leitstand.service;

import de.gedoplan.v5t11.leitstand.messaging.OutgoingHandler;
import de.gedoplan.v5t11.util.jsf.NavigationItem;

import org.jboss.logging.Logger;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Default;
import jakarta.inject.Inject;

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

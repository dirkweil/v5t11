package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.messaging.OutgoingHandler;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.TransactionPhase;
import javax.enterprise.inject.Any;
import javax.inject.Inject;

@ApplicationScoped
public class EventDispatcher {

  @Inject
  OutgoingHandler outgoingHandler;

  void dispatch(@Observes(during = TransactionPhase.AFTER_SUCCESS) @Any Fahrstrasse fahrstrasse) {
    this.outgoingHandler.publish(fahrstrasse);
  }
}

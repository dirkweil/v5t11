package de.gedoplan.v5t11.parcours.service;

import de.gedoplan.v5t11.parcours.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.parcours.messaging.OutgoingHandler;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.event.TransactionPhase;
import jakarta.enterprise.inject.Any;
import jakarta.inject.Inject;

@ApplicationScoped
public class EventDispatcher {

  @Inject
  OutgoingHandler outgoingHandler;

  void dispatch(@Observes(during = TransactionPhase.AFTER_SUCCESS) @Any Fahrstrasse fahrstrasse) {
    this.outgoingHandler.publish(fahrstrasse);
  }
}

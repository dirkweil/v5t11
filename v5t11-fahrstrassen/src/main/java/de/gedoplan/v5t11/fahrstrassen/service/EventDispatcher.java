package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.messaging.OutgoingHandler;

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

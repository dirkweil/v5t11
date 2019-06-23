package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Kanal;
import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

@ApplicationScoped
public class EventDispatcher {
  @Inject
  Log log;

  @Inject
  Steuerung steuerung;

  void logEvent(@Observes Kanal kanal) {
    this.log.debug("Event: " + kanal);
    this.steuerung.adjustWert(kanal.getAdresse(), kanal.getWert());
  }

  void logEvent(@Observes Zentrale event) {
    this.log.debug("Event: " + event);
  }
}

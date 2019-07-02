package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Kanal;
import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.lok.Lok;

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

  void dispatch(@Observes Kanal kanal) {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Event: " + kanal);
    }
    this.steuerung.adjustWert(kanal.getAdresse(), kanal.getWert());
  }

  void dispatch(@Observes Zentrale event) {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Event: " + event);
    }
  }

  void dispatch(@Observes Lok event) {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Event: " + event.toString(false) + " aktiv=" + event.isAktiv());
    }

    this.steuerung.getZentrale().lokChanged(event);
  }
}

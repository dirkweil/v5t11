package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Kanal;
import de.gedoplan.v5t11.status.entity.SX2Kanal;
import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.logging.Logger;

@ApplicationScoped
public class EventDispatcher {

  @Inject
  Logger log;

  @Inject
  Steuerung steuerung;

  void dispatch(@Observes Kanal kanal) {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Event: " + kanal);
    }
    this.steuerung.adjustTo(kanal);
  }

  void dispatch(@Observes SX2Kanal kanal) {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Event: " + kanal);
    }
    this.steuerung.adjustTo(kanal);
  }

  void dispatch(@Observes Zentrale event) {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Event: " + event);
    }
  }

  void dispatch(@Observes Fahrzeug event) {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Event: " + event);
    }

    this.steuerung.getZentrale().lokChanged(event);
  }
}

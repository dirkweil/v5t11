package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Kanal;
import de.gedoplan.v5t11.status.entity.SX2Kanal;
import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.status.messaging.OutgoingHandler;
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
  Steuerung steuerung;

  @Inject
  OutgoingHandler outgoingHandler;

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

  void dispatch(@Observes Gleisabschnitt gleisabschnitt) {
    this.outgoingHandler.publish(gleisabschnitt);
  }

  void dispatch(@Observes Signal signal) {
    this.outgoingHandler.publish(signal);
  }

  void dispatch(@Observes Weiche weiche) {
    this.outgoingHandler.publish(weiche);
  }

  void dispatch(@Observes Zentrale zentrale) {
    this.outgoingHandler.publish(zentrale);
  }

  void dispatch(@Observes NavigationItem navigationItem) {
    this.outgoingHandler.publish(navigationItem);
  }

  void dispatch(@Observes Fahrzeug fahrzeug) {
    this.steuerung.getZentrale().lokChanged(fahrzeug);
    this.outgoingHandler.publish(fahrzeug);
  }
}

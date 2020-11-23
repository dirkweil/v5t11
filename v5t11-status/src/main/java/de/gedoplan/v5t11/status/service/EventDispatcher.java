package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Kanal;
import de.gedoplan.v5t11.status.entity.SX2Kanal;
import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
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

  @Inject
  StatusPublisher statusPublisher;

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
    this.statusPublisher.publish(gleisabschnitt);
  }

  void dispatch(@Observes Signal signal) {
    this.statusPublisher.publish(signal);
  }

  void dispatch(@Observes Weiche weiche) {
    this.statusPublisher.publish(weiche);
  }

  void dispatch(@Observes Zentrale zentrale) {
    this.statusPublisher.publish(zentrale);
  }

  void dispatch(@Observes Fahrzeug fahrzeug) {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Event: " + fahrzeug);
    }

    this.steuerung.getZentrale().lokChanged(fahrzeug);
  }
}

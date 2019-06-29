package de.gedoplan.v5t11.status.testenvironment.service;

import de.gedoplan.v5t11.status.entity.Kanal;
import de.gedoplan.v5t11.status.entity.SX2Kanal;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.lok.Lok;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

@ApplicationScoped
public class V5t11EventLogger {
  @Inject
  Log log;

  void logEvent(@Observes Kanal event) {
    this.log.debug("Event: " + event);
  }

  void logEvent(@Observes SX2Kanal event) {
    this.log.debug("Event: " + event);
  }

  void logEvent(@Observes Zentrale event) {
    this.log.debug("Event: " + event);
  }

  void logEvent(@Observes Gleisabschnitt event) {
    this.log.debug("Event: " + event + " besetzt=" + event.isBesetzt());
  }

  void logEvent(@Observes Weiche event) {
    this.log.debug("Event: " + event + " stellung=" + event.getStellung());
  }

  void logEvent(@Observes Signal event) {
    this.log.debug("Event: " + event + " stellung=" + event.getStellung());
  }

  void logEvent(@Observes Lok event) {
    this.log.debug("Event: " + event.toString(false) + " aktiv=" + event.isAktiv());
  }
}

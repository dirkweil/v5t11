package de.gedoplan.v5t11.status.testenvironment.service;

import de.gedoplan.v5t11.status.entity.SX2Kanal;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.service.EventDispatcher;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.logging.Logger;

/**
 * Event-Logger.
 *
 * Es werden zu Debug-Zwecken die Events geloggt, die nicht von {@link EventDispatcher} protokolliert werden.
 *
 * @author dw
 *
 */
@ApplicationScoped
public class V5t11EventLogger {

  @Inject
  Logger log;

  void logEvent(@Observes SX2Kanal event) {
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
}

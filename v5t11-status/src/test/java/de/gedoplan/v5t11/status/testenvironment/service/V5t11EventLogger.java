package de.gedoplan.v5t11.status.testenvironment.service;

import de.gedoplan.v5t11.status.entity.Kanal;
import de.gedoplan.v5t11.status.entity.SX2Kanal;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.domain.entity.Bereichselement;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.spi.EventMetadata;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

/**
 * Event-Logger.
 *
 * @author dw
 */
@ApplicationScoped
public class V5t11EventLogger {

  @Inject
  Logger logger;

  void logEvent(@Observes Kanal event, EventMetadata eventMetadata) {
    log(event, eventMetadata);
  }

  void logEvent(@Observes SX2Kanal event, EventMetadata eventMetadata) {
    log(event, eventMetadata);
  }

  void logEvent(@Observes Bereichselement event, EventMetadata eventMetadata) {
    log(event, eventMetadata);
  }

  void logEvent(@Observes Fahrzeug event, EventMetadata eventMetadata) {
    log(event, eventMetadata);
  }

  void logEvent(@Observes Zentrale event, EventMetadata eventMetadata) {
    log(event, eventMetadata);
  }

  private void log(Object event, EventMetadata eventMetadata) {
    this.logger.debugf("Event: %s, qualifier: %s", event, eventMetadata.getQualifiers());
  }
}

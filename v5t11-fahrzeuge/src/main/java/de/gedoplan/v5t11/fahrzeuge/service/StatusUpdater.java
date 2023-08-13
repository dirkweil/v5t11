package de.gedoplan.v5t11.fahrzeuge.service;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.messaging.IncomingHandler;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.cdi.Received;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

/**
 * Aktualisierung der Status von Fahrzeugen, Gleisen etc.
 * <p>
 * Die Aktualisierung wird durch eingehende Meldungen (von v5t11-status gesendet) ausgelöst. {@link IncomingHandler}
 * wandelt die Meldungen in CDI Event um, die hier verarbeitet werden.
 *
 * @author dw
 */
@ApplicationScoped
@Transactional(rollbackOn = Exception.class)
public class StatusUpdater {

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Inject
  Logger logger;

  @Inject
  EventFirer eventFirer;

  /**
   * Aktualisierung eines Fahrzeugs.
   *
   * @param receivedObject Empfangenes Objekt mit dem neuen Status.
   */
  void fahrzeugReceived(@ObservesAsync @Received Fahrzeug receivedObject) {
    this.fahrzeugRepository
      .findById(receivedObject.getId())
      .ifPresent(fahrzeug -> copyStatus(fahrzeug, receivedObject));
  }

  private void copyStatus(Fahrzeug to, Fahrzeug from) {
    if (to != null) {
      if (to.copyStatus(from)) {
        if (this.logger.isDebugEnabled()) {
          this.logger.debug(to);
        }

        this.eventFirer.fire(to, Changed.Literal.INSTANCE);
      }
    }

  }

  // private void copyStatus(Fahrwegelement to, Fahrwegelement from) {
  // if (to != null) {
  // if (to.copyStatus(from)) {
  // if (this.logger.isDebugEnabled()) {
  // this.logger.debug(to);
  // }
  //
  // this.eventFirer.fire(to);
  // }
  // }
  //
  // }
}

package de.gedoplan.v5t11.fahrzeuge.service;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeugdecoder;
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
  void fahrzeugReceived(@ObservesAsync @Received Fahrzeugdecoder receivedObject) {
    if (receivedObject.getDecoderAdr() != null) {
      this.fahrzeugRepository
        .findByDecoderId(receivedObject.getDecoderAdr())
        .forEach(fahrzeug -> copyStatus(fahrzeug, receivedObject));
    } else {
      this.logger.warnf("Fahrzeug ohne id und decoderId empfangen - wird ignoriert: %s", receivedObject);
    }
  }

  private void copyStatus(Fahrzeug to, Fahrzeugdecoder from) {
    if (to != null) {
      if (to.getFahrzeugdecoder().copyStatus(from)) {
        if (this.logger.isDebugEnabled()) {
          this.logger.debug(to);
        }

        this.eventFirer.fire(to, Changed.Literal.INSTANCE);
      }
    }

  }
}

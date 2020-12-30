package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.status.messaging.IncomingHandler;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.cdi.Received;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.jboss.logging.Logger;

/**
 * Aktualisierung der Status von Fahrzeugen etc.
 * 
 * Die Aktualisierung wird durch eingehende Meldungen (von v5t11-status gesendet) ausgelöst. {@link IncomingHandler}
 * wandelt die Meldungen in CDI Event um, die hier verarbeitet werden.
 * 
 * @author dw
 */
@ApplicationScoped
@Transactional(rollbackOn = Exception.class)
public class StatusUpdater {

  @Inject
  Steuerung steuerung;

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
    this.logger.debugf("Received %s", receivedObject);

    // TODO Löschen implementieren
    this.steuerung.getOrCreateFahrzeug(receivedObject.getId());
  }
}

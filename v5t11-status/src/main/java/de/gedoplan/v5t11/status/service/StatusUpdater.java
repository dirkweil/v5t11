package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeugdecoder;
import de.gedoplan.v5t11.status.messaging.IncomingHandler;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.cdi.Received;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

/**
 * Aktualisierung der Status von Fahrzeugen etc.
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
  void fahrzeugReceived(@ObservesAsync @Received Fahrzeugdecoder receivedObject) {
    this.logger.debugf("Received %s", receivedObject);

    // TODO Löschen implementieren
    this.steuerung.getOrCreateFahrzeugdecoder(receivedObject.getId());
  }

  private boolean zentraleImNormalbetrieb = false;

  void zentraleChanged(@ObservesAsync @Changed Zentrale zentrale) {
    // Wenn wieder Normalbetrieb, Besetztmelder aktualisieren
    boolean normalbetrieb = zentrale.isNormalbetrieb();
    if (!this.zentraleImNormalbetrieb && normalbetrieb) {
      this.steuerung.adjustAllBesetztmelderStatus();
    }
    this.zentraleImNormalbetrieb = normalbetrieb;
  }
}

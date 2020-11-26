package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.fahrstrassen.persistence.GleisabschnittRepository;
import de.gedoplan.v5t11.fahrstrassen.persistence.SignalRepository;
import de.gedoplan.v5t11.fahrstrassen.persistence.WeicheRepository;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.domain.entity.Fahrwegelement;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.jboss.logging.Logger;

/**
 * Aktualisierung der Status von Gleisabschnitten, Signalen etc.
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
  GleisabschnittRepository gleisabschnittRepository;

  @Inject
  SignalRepository signalRepository;

  @Inject
  WeicheRepository weicheRepository;

  @Inject
  Logger logger;

  @Inject
  EventFirer eventFirer;

  /**
   * Aktualisierung eines Gleisabschnitts.
   * 
   * @param receivedObject Empfangenes Objekt mit dem neuen Status.
   */
  void gleisabschnittReceived(@ObservesAsync @Received Gleisabschnitt receivedObject) {
    Gleisabschnitt gleisabschnitt = this.gleisabschnittRepository.findById(receivedObject.getId());
    copyStatus(gleisabschnitt, receivedObject);
  }

  /**
   * Aktualisierung eines Signals.
   * 
   * @param receivedObject Empfangenes Objekt mit dem neuen Status.
   */
  void signalReceived(@ObservesAsync @Received Signal receivedObject) {
    Signal signal = this.signalRepository.findById(receivedObject.getId());
    copyStatus(signal, receivedObject);
  }

  /**
   * Aktualisierung einer Weiche.
   * 
   * @param receivedObject Empfangenes Objekt mit dem neuen Status.
   */
  void weicheReceived(@ObservesAsync @Received Weiche receivedObject) {
    Weiche signal = this.weicheRepository.findById(receivedObject.getId());
    copyStatus(signal, receivedObject);
  }

  private void copyStatus(Fahrwegelement to, Fahrwegelement from) {
    if (to != null) {
      if (to.copyStatus(from)) {
        if (this.logger.isDebugEnabled()) {
          this.logger.debug(to);
        }

        this.eventFirer.fire(to);
      }
    }

  }
}

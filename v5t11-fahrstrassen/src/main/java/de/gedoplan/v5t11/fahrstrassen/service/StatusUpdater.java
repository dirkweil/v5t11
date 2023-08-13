package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.fahrstrassen.messaging.IncomingHandler;
import de.gedoplan.v5t11.fahrstrassen.persistence.GleisRepository;
import de.gedoplan.v5t11.fahrstrassen.persistence.SignalRepository;
import de.gedoplan.v5t11.fahrstrassen.persistence.WeicheRepository;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.cdi.Received;
import de.gedoplan.v5t11.util.domain.entity.Fahrwegelement;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

/**
 * Aktualisierung der Status von Gleisen, Signalen etc.
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
  GleisRepository gleisRepository;

  @Inject
  SignalRepository signalRepository;

  @Inject
  WeicheRepository weicheRepository;

  @Inject
  Logger logger;

  @Inject
  EventFirer eventFirer;

  /**
   * Aktualisierung eines Gleiss.
   *
   * @param receivedObject Empfangenes Objekt mit dem neuen Status.
   */
  void gleisReceived(@ObservesAsync @Received Gleis receivedObject) {
    this.gleisRepository
      .findById(receivedObject.getId())
      .ifPresent(gleis -> copyStatus(gleis, receivedObject));
  }

  /**
   * Aktualisierung eines Signals.
   *
   * @param receivedObject Empfangenes Objekt mit dem neuen Status.
   */
  void signalReceived(@ObservesAsync @Received Signal receivedObject) {
    this.signalRepository
      .findById(receivedObject.getId())
      .ifPresent(signal -> copyStatus(signal, receivedObject));
  }

  /**
   * Aktualisierung einer Weiche.
   *
   * @param receivedObject Empfangenes Objekt mit dem neuen Status.
   */
  void weicheReceived(@ObservesAsync @Received Weiche receivedObject) {
    this.weicheRepository
      .findById(receivedObject.getId())
      .ifPresent(weiche -> copyStatus(weiche, receivedObject));
  }

  private void copyStatus(Fahrwegelement to, Fahrwegelement from) {
    if (to != null) {
      if (to.copyStatus(from)) {
        if (this.logger.isDebugEnabled()) {
          this.logger.debug(to);
        }

        this.eventFirer.fire(to, Changed.Literal.INSTANCE);
      }
    }

  }
}

package de.gedoplan.v5t11.leitstand.service;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.baustein.Zentrale;
import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.persistence.GleisRepository;
import de.gedoplan.v5t11.leitstand.persistence.SignalRepository;
import de.gedoplan.v5t11.leitstand.persistence.WeicheRepository;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.cdi.Received;
import de.gedoplan.v5t11.util.domain.entity.Fahrwegelement;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.jboss.logging.Logger;

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
  FahrstrassenManager fahrstrassenManager;

  @Inject
  Leitstand leitstand;

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
    Gleis gleis = this.gleisRepository.findById(receivedObject.getId());
    copyStatus(gleis, receivedObject);
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

  /**
   * Aktualisierung der Zentrale.
   * 
   * @param receivedObject Empfangenes Objekt mit dem neuen Status.
   */
  void zentraleReceived(@ObservesAsync @Received Zentrale receivedObject) {
    Zentrale zentrale = this.leitstand.getZentrale();
    zentrale.copyStatus(receivedObject);
    this.eventFirer.fire(zentrale, Changed.Literal.INSTANCE);
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

  /**
   * Aktualisierung einer Fahrstrasse.
   * 
   * @param receivedObject Empfangenes Objekt mit dem neuen Status.
   */
  void FahrstrasseReceived(@ObservesAsync @Received Fahrstrasse receivedObject) {
    this.fahrstrassenManager.updateFahrstrasse(receivedObject);
  }

}
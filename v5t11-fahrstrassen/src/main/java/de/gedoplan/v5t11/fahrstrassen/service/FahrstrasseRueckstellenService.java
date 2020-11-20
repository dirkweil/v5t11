package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse.Freigegeben;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.FahrstrassenSignal;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.fahrstrassen.gateway.StatusGateway;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;

import java.lang.annotation.Annotation;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.enterprise.inject.spi.EventMetadata;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class FahrstrasseRueckstellenService {

  @Inject
  @RestClient
  StatusGateway statusGateway;

  @Inject
  Logger log;

  void fahrstrasseRueckstellen(@ObservesAsync @Freigegeben Fahrstrasse fahrstrasse, EventMetadata eventMetadata) {

    Freigegeben freigegeben = null;
    for (Annotation annotation : eventMetadata.getQualifiers()) {
      if (annotation instanceof Freigegeben) {
        freigegeben = (Freigegeben) annotation;
        break;
      }
    }

    if (this.log.isDebugEnabled()) {
      this.log.debug("Fahrstrasse rueckstellen: " + fahrstrasse + ", bisher=" + freigegeben.bisher() + ", neu=" + freigegeben.neu());
    }

    // Nicht-Schutz-Signale auf HALT stellen
    fahrstrasse.getElemente().stream()
        .limit(freigegeben.neu())
        .skip(freigegeben.bisher())
        .filter(fe -> fe instanceof FahrstrassenSignal)
        .filter(fe -> !fe.isSchutz())
        .filter(fe -> fe.getFahrwegelement().getReserviertefahrstrasseId() == null)
        .map(fe -> (FahrstrassenSignal) fe)
        .filter(fs -> !fs.isVorsignal()) // TODO Vorsignalhandling
        .forEach(fs -> signalRueckstellen(fahrstrasse, fs));
  }

  private void signalRueckstellen(Fahrstrasse fahrstrasse, FahrstrassenSignal fahrstrassenSignal) {
    Signal signal = fahrstrassenSignal.getFahrwegelement();
    SignalStellung stellung = SignalStellung.HALT;
    try {
      if (this.log.isDebugEnabled()) {
        this.log.debug("Stelle " + signal + " auf " + stellung);
      }

      this.statusGateway.signalStellen(
          signal.getBereich(), signal.getName(),
          stellung);
    } catch (Exception e) {
      this.log.error("Kann " + signal + " nicht stellen", e);
    }
    delay();
  }

  private static void delay() {
    try {
      Thread.sleep(250);
    } catch (InterruptedException e) {
    }
  }
}

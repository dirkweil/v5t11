package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse.Reserviert;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.FahrstrassenSignal;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.FahrstrassenWeiche;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrassenelement;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.fahrstrassen.gateway.StatusGateway;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class FahrstrasseStellenService {

  /*
   * Listen von Signalstellungen für bestimmte Fahrsituationen, jeweils mit der am besten passenden Stellung zuerst.
   */
  private static final List<SignalStellung> HALT_LIST = Stream.of(SignalStellung.HALT).collect(Collectors.toList());
  private static final List<SignalStellung> FAHRT_LIST = Stream.of(SignalStellung.FAHRT, SignalStellung.LANGSAMFAHRT, SignalStellung.RANGIERFAHRT).collect(Collectors.toList());
  private static final List<SignalStellung> LANGSAMFAHRT_LIST = Stream.of(SignalStellung.LANGSAMFAHRT, SignalStellung.FAHRT, SignalStellung.RANGIERFAHRT).collect(Collectors.toList());
  private static final List<SignalStellung> RANGIERFAHRT_LIST = Stream.of(SignalStellung.RANGIERFAHRT, SignalStellung.LANGSAMFAHRT, SignalStellung.FAHRT).collect(Collectors.toList());

  @Inject
  @RestClient
  StatusGateway statusGateway;

  @Inject
  Logger log;

  void fahrstrasseStellen(@ObservesAsync @Reserviert Fahrstrasse fahrstrasse) {
    if (this.log.isDebugEnabled()) {
      this.log.debug("Fahrstrasse stellen: " + fahrstrasse);
    }

    // Schutzsignale stellen
    signaleStellen(fahrstrasse, true);

    // Schutzweichen stellen
    weichenStellen(fahrstrasse, true);

    // Nicht-Schutz-Weichen stellen
    weichenStellen(fahrstrasse, false);

    // Nicht-Schutz-Signale stellen
    signaleStellen(fahrstrasse, false);
  }

  private void signaleStellen(Fahrstrasse fahrstrasse, boolean schutz) {
    Stream<Fahrstrassenelement> stream = fahrstrasse.getElemente().stream()
      .filter(fe -> fe instanceof FahrstrassenSignal)
      .filter(fe -> fe.isSchutz() == schutz);

    stream
      .map(fe -> (FahrstrassenSignal) fe)
      .filter(fs -> !fs.isVorsignal())
      .forEach(fs -> signalStellen(fahrstrasse, fs));
  }

  private void signalStellen(Fahrstrasse fahrstrasse, FahrstrassenSignal fahrstrassenSignal) {
    Signal signal = fahrstrassenSignal.getFahrwegelement();
    List<SignalStellung> stellungen = getAngepassteStellung(fahrstrassenSignal.getStellung(), fahrstrasse.getFahrstrassenStatus().getReservierungsTyp());
    try {
      if (this.log.isDebugEnabled()) {
        this.log.debug("Stelle " + signal + " auf " + stellungen);
      }

      this.statusGateway.signalStellen(
        signal.getBereich(), signal.getName(),
        stellungen);
    } catch (Exception e) {
      this.log.error("Kann " + signal + " nicht stellen", e);
    }

    delay();
  }

  private void weichenStellen(Fahrstrasse fahrstrasse, boolean schutz) {
    Stream<Fahrstrassenelement> stream = fahrstrasse.getElemente().stream()
      .filter(fe -> fe instanceof FahrstrassenWeiche)
      .filter(fe -> fe.isSchutz() == schutz);

    stream
      .map(fe -> (FahrstrassenWeiche) fe)
      .forEach(fw -> weicheStellen(fahrstrasse, fw));
  }

  private void weicheStellen(Fahrstrasse fahrstrasse, FahrstrassenWeiche fahrstrassenWeiche) {
    Weiche weiche = fahrstrassenWeiche.getFahrwegelement();
    WeichenStellung stellung = fahrstrassenWeiche.getStellung();
    try {
      if (this.log.isDebugEnabled()) {
        this.log.debug("Stelle " + weiche + " auf " + stellung);
      }

      this.statusGateway.weicheStellen(weiche.getBereich(), weiche.getName(), stellung);
    } catch (Exception e) {
      this.log.error("Kann " + weiche + " nicht stellen", e);
    }
    delay();
  }

  private static void delay() {
    try {
      Thread.sleep(250);
    } catch (InterruptedException e) {
    }
  }

  /**
   * Stellung passend zum Reservierungstyp liefern.
   *
   * @param stellung gewünschte Stellung
   * @param reservierungsTyp Reservierungstyp, falls Signal für eine Fahrstrasse gestellt wird, sonst <code>null</code>
   * @return angepasste Stellung
   */
  private static List<SignalStellung> getAngepassteStellung(SignalStellung stellung, FahrstrassenReservierungsTyp reservierungsTyp) {

    // HALT ist immer OK
    if (stellung == SignalStellung.HALT) {
      return HALT_LIST;
    }

    switch (reservierungsTyp) {
    default:
    case ZUGFAHRT:
      switch (stellung) {
      default:
      case FAHRT:
        return FAHRT_LIST;

      case LANGSAMFAHRT:
        return LANGSAMFAHRT_LIST;

      case RANGIERFAHRT:
        return RANGIERFAHRT_LIST;
      }

    case RANGIERFAHRT:
      return RANGIERFAHRT_LIST;
    }
  }

}

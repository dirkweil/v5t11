package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse.Reserviert;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.FahrstrassenSignal;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.FahrstrassenWeiche;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrassenelement;
import de.gedoplan.v5t11.fahrstrassen.gateway.SignalResourceClient;
import de.gedoplan.v5t11.fahrstrassen.gateway.WeicheResourceClient;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

@ApplicationScoped
public class FahrstrasseStellenService {

  @Inject
  SignalResourceClient signalResourceClient;

  @Inject
  WeicheResourceClient weicheResourceClient;

  @Inject
  Log log;

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

    // Für Nicht-Schutz-Elemente prüfen, ob das Element wirklich zur Fahrstrasse gehört
    // TODO ist das nötig?
    if (!schutz) {
      stream = stream.filter(fe -> fe.getFahrwegelement().getReserviertefahrstrasse() == fahrstrasse);
    }

    stream
        .map(fe -> (FahrstrassenSignal) fe)
        .filter(fs -> !fs.isVorsignal()) // TODO Vorsignalhandling
        .forEach(fs -> signalStellen(fahrstrasse, fs));
  }

  private void signalStellen(Fahrstrasse fahrstrasse, FahrstrassenSignal fahrstrassenSignal) {
    this.signalResourceClient.signalStellen(
        fahrstrassenSignal.getFahrwegelement(),
        getAngepassteStellung(fahrstrassenSignal.getStellung(), fahrstrasse.getReservierungsTyp()));

    delay();
  }

  private void weichenStellen(Fahrstrasse fahrstrasse, boolean schutz) {
    Stream<Fahrstrassenelement> stream = fahrstrasse.getElemente().stream()
        .filter(fe -> fe instanceof FahrstrassenWeiche)
        .filter(fe -> fe.isSchutz() == schutz);

    // Für Nicht-Schutz-Elemente prüfen, ob das Element wirklich zur Fahrstrasse gehört
    // TODO ist das nötig?
    if (!schutz) {
      stream = stream.filter(fe -> fe.getFahrwegelement().getReserviertefahrstrasse() == fahrstrasse);
    }

    stream
        .map(fe -> (FahrstrassenWeiche) fe)
        .forEach(fw -> weicheStellen(fahrstrasse, fw));
  }

  private void weicheStellen(Fahrstrasse fahrstrasse, FahrstrassenWeiche fahrstrassenWeiche) {
    this.weicheResourceClient.weicheStellen(fahrstrassenWeiche.getFahrwegelement(), fahrstrassenWeiche.getStellung());
    delay();
  }

  private static void delay() {
    try {
      Thread.sleep(250);
    } catch (InterruptedException e) {}
  }

  /**
   * Stellung passend zum Reservierungstyp liefern.
   *
   * @param stellung
   *          gewünschte Stellung
   * @param reservierungsTyp
   *          Reservierungstyp, falls Signal für eine Fahrstrasse gestellt wird, sonst <code>null</code>
   * @return angepasste Stellung
   */
  private static List<SignalStellung> getAngepassteStellung(SignalStellung stellung, FahrstrassenReservierungsTyp reservierungsTyp) {

    // HALT ist immer OK
    if (stellung == SignalStellung.HALT) {
      return Stream.of(SignalStellung.HALT).collect(Collectors.toList());
    }

    switch (reservierungsTyp) {
    default:
    case ZUGFAHRT:
      switch (stellung) {
      default:
      case FAHRT:
        return Stream.of(SignalStellung.FAHRT, SignalStellung.LANGSAMFAHRT, SignalStellung.RANGIERFAHRT).collect(Collectors.toList());

      case LANGSAMFAHRT:
        return Stream.of(SignalStellung.LANGSAMFAHRT, SignalStellung.FAHRT, SignalStellung.RANGIERFAHRT).collect(Collectors.toList());

      case RANGIERFAHRT:
        return Stream.of(SignalStellung.RANGIERFAHRT, SignalStellung.LANGSAMFAHRT, SignalStellung.FAHRT).collect(Collectors.toList());
      }

    case RANGIERFAHRT:
      return Stream.of(SignalStellung.RANGIERFAHRT, SignalStellung.LANGSAMFAHRT, SignalStellung.FAHRT).collect(Collectors.toList());
    }
  }

}

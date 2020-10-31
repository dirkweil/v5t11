package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse.Freigegeben;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse.Reserviert;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.FahrstrassenGleisabschnitt;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrassenelement;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.EventMetadata;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import lombok.Getter;

@ApplicationScoped
public class FahrstrasseMonitor {

  private Map<Gleisabschnitt, GleisabschnittStatus> statusMap = new HashMap<>();

  @Inject
  Log log;

  /**
   * Überwachung einer Fahrstrasse beginnen.
   *
   * @param fahrstrasse
   *          Fahrstrasse
   */
  void start(@Observes @Reserviert Fahrstrasse fahrstrasse) {
    // Alle Gleisabschnitte der Fahrstrasse in statusMap eintragen
    Stream<Gleisabschnitt> stream = fahrstrasse.getElemente().stream()
        .filter(fe -> fe instanceof FahrstrassenGleisabschnitt)
        .map(fe -> ((FahrstrassenGleisabschnitt) fe).getFahrwegelement());

    if (this.log.isDebugEnabled()) {
      stream = stream.peek(g -> this.log.debug("start: " + g + " überwachen"));
    }

    stream.forEach(g -> this.statusMap.put(g, new GleisabschnittStatus(g)));
  }

  void stop(@Observes @Freigegeben Fahrstrasse fahrstrasse, EventMetadata eventMetadata) {

    Freigegeben freigegeben = null;
    for (Annotation annotation : eventMetadata.getQualifiers()) {
      if (annotation instanceof Freigegeben) {
        freigegeben = (Freigegeben) annotation;
        break;
      }
    }

    // Alle freigegebenen Gleisabschnitte der Fahrstrasse aus statusMap entfernen
    Stream<Gleisabschnitt> stream = fahrstrasse.getElemente().stream()
        .limit(freigegeben.neu())
        .skip(freigegeben.bisher())
        .filter(fe -> fe instanceof FahrstrassenGleisabschnitt)
        .map(fe -> ((FahrstrassenGleisabschnitt) fe).getFahrwegelement());

    if (this.log.isDebugEnabled()) {
      stream = stream.peek(g -> this.log.debug("stop: " + g + " nicht mehr überwachen"));
    }

    stream.forEach(g -> this.statusMap.remove(g));
  }

  /**
   * Auf Belegtänderung eines Gleisabschnitts reagieren.
   *
   * @param gleisabschnitt
   *          Gleisabschnitt
   */
  void processGleisabschnitt(@Observes Gleisabschnitt gleisabschnitt) {
    // Status zum Gleisabschnitt heraussuchen
    GleisabschnittStatus gleisabschnittStatus = this.statusMap.get(gleisabschnitt);
    if (gleisabschnittStatus == null) {
      return;
    }

    // Status aktualisieren
    gleisabschnittStatus.update(gleisabschnitt);
    // if (!gleisabschnittStatus.isDurchfahren()) {
    // return;
    // }

    checkFreigabe(gleisabschnitt.getReserviertefahrstrasse());
  }

  private void checkFreigabe(Fahrstrasse fahrstrasse) {
    /*
     * Im reservierten Teil der Fahrstrasse den Gleisabschnitt suchen, der noch nicht durchfahren wurde,
     * und vor dem nur durchfahrene Gleisabschnitte liegen.
     */
    int elementAnzahl = fahrstrasse.getElemente().size();
    int idxLetzter = fahrstrasse.getTeilFreigabeAnzahl();
    int idxGrenze = fahrstrasse.getTeilFreigabeAnzahl();
    Gleisabschnitt grenze = null;
    while (idxGrenze < elementAnzahl) {
      Fahrstrassenelement fe = fahrstrasse.getElemente().get(idxGrenze);
      if (fe instanceof FahrstrassenGleisabschnitt) {
        Gleisabschnitt g = ((FahrstrassenGleisabschnitt) fe).getFahrwegelement();
        GleisabschnittStatus gs = this.statusMap.get(g);

        if (gs.isDurchfahren()) {
          idxLetzter = idxGrenze;
        } else {
          grenze = g;
          break;
        }
      }

      ++idxGrenze;
    }

    /*
     * Komplettfreigabe ist möglich,
     * - wenn ab dem ersten nicht durchfahrerenen Abschnitt alles besetzt ist
     * - oder wenn nur noch Gleisabschnitte folgen.
     */
    boolean totalFreigabe = fahrstrasse.isKomplettBesetzt(idxGrenze) || fahrstrasse.isNurGleisabschnitte(idxLetzter + 1);

    if (this.log.isDebugEnabled()) {
      this.log.debug("checkFreigabe: grenze=" + grenze + ", totalFreigabe=" + totalFreigabe);
    }

    fahrstrasse.freigeben(totalFreigabe ? null : grenze);
  }

  @Getter
  private class GleisabschnittStatus {
    private boolean besetzt;
    private boolean durchfahren;

    public GleisabschnittStatus(Gleisabschnitt gleisabschnitt) {
      this.besetzt = gleisabschnitt.isBesetzt();

      if (FahrstrasseMonitor.this.log.isDebugEnabled()) {
        FahrstrasseMonitor.this.log.debug(String.format("Status von %s: besetzt=%b, durchfahren=%b", gleisabschnitt, this.besetzt, this.durchfahren));
      }
    }

    public void update(Gleisabschnitt gleisabschnitt) {
      if (gleisabschnitt.isBesetzt()) {
        this.besetzt = true;
        this.durchfahren = false;
      } else {
        if (this.besetzt) {
          this.durchfahren = true;
        }
        this.besetzt = false;
      }

      if (FahrstrasseMonitor.this.log.isDebugEnabled()) {
        FahrstrasseMonitor.this.log.debug(String.format("Status von %s: besetzt=%b, durchfahren=%b", gleisabschnitt, this.besetzt, this.durchfahren));
      }
    }
  }
}

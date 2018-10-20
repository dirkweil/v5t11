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
    int i = fahrstrasse.getTeilFreigabeAnzahl();
    Gleisabschnitt grenze = null;
    while (i < elementAnzahl) {
      Fahrstrassenelement fe = fahrstrasse.getElemente().get(i);
      if (fe instanceof FahrstrassenGleisabschnitt) {
        Gleisabschnitt g = ((FahrstrassenGleisabschnitt) fe).getFahrwegelement();
        GleisabschnittStatus gs = this.statusMap.get(g);

        if (!gs.isDurchfahren()) {
          grenze = g;
          break;
        }
      }

      ++i;
    }

    /*
     * Sind ab dort nur besetzte normale (Nicht-Weichen-) Gleisabschnitte (und keine Nicht-Schutz-Weichen oder -Signale),
     * kann die Fahrstrasse komplett freigegeben werden
     */
    boolean totalFreigabe = true;
    while (i < elementAnzahl) {
      Fahrstrassenelement fe = fahrstrasse.getElemente().get(i);
      if (fe instanceof FahrstrassenGleisabschnitt) {
        // fe ist Gleisabschnitt; wenn der ein Weichen-Gleisabschnitt ist oder nicht besetzt ist,
        // kann nicht komplett freigebeben werden
        Gleisabschnitt g = ((FahrstrassenGleisabschnitt) fe).getFahrwegelement();
        if (g.isWeichenGleisabschnitt() || !g.isBesetzt()) {
          totalFreigabe = false;
          break;
        }
      } else {
        // fe ist ein Signal oder eine Weiche; wenn dies nicht nur ein Schutz-Element ist, kann nicht komplett freigebeben werden
        if (!fe.isSchutz()) {
          totalFreigabe = false;
          break;
        }
      }

      ++i;
    }

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

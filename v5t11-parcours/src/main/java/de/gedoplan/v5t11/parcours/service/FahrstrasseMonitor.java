package de.gedoplan.v5t11.parcours.service;

import de.gedoplan.v5t11.parcours.entity.Parcours;
import de.gedoplan.v5t11.parcours.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.parcours.entity.fahrstrasse.FahrstrassenGleis;
import de.gedoplan.v5t11.parcours.entity.fahrstrasse.Fahrstrassenelement;
import de.gedoplan.v5t11.parcours.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import java.util.stream.IntStream;

import org.jboss.logging.Logger;

@ApplicationScoped
public class FahrstrasseMonitor {

  @Inject
  Logger log;

  @Inject
  Parcours parcours;

  /**
   * Auf Belegtänderung eines Gleises reagieren.
   *
   * @param gleis Gleis
   */
  void processGleis(@Observes @Changed Gleis gleis) {

    // Wenn Gleis nicht Teil einer Fahrstrasse ist, nichts tun
    BereichselementId fahrstrasseId = gleis.getReserviertefahrstrasseId();
    if (fahrstrasseId == null) {
      return;
    }

    Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse(fahrstrasseId.getBereich(), fahrstrasseId.getName());
    if (fahrstrasse == null) {
      return;
    }

    this.log.debugf("Freigabecheck für %s", fahrstrasseId);

    /*
     * Im reservierten Teil der Fahrstrasse das Gleis suchen, das noch nicht durchfahren wurde,
     * und vor dem nur durchfahrene Gleise liegen.
     */
    int elementAnzahl = fahrstrasse.getElemente().size();
    int idxGrenze = fahrstrasse.getTeilFreigabeAnzahl();

    if (this.log.isDebugEnabled()) {
      this.log.debugf("  elementAnzahl=%d, idxGrenze=%d", elementAnzahl, idxGrenze);
      IntStream.range(idxGrenze, elementAnzahl)
        .mapToObj(i -> fahrstrasse.getElemente().get(i))
        .filter(fe -> fe instanceof FahrstrassenGleis)
        .map(fe -> ((FahrstrassenGleis) fe).getFahrwegelement())
        .forEach(g -> this.log.debugf("  %s: besetzt=%s, durchfahren=%s", g, g.isBesetzt(), g.isDurchfahren()));
    }

    Gleis grenze = null;
    while (idxGrenze < elementAnzahl) {
      Fahrstrassenelement fe = fahrstrasse.getElemente().get(idxGrenze);
      if (fe instanceof FahrstrassenGleis) {
        Gleis g = ((FahrstrassenGleis) fe).getFahrwegelement();
        if (!g.isDurchfahren()) {
          grenze = g;
          break;
        }
      }

      ++idxGrenze;
    }

    /*
     * Komplettfreigabe ist möglich,
     * - wenn ab dem ersten nicht durchfahrenen Abschnitt alles besetzt ist
     * - oder wenn nur noch Gleise folgen.
     */
    boolean totalFreigabe = fahrstrasse.isKomplettBesetzt(idxGrenze) || fahrstrasse.isNurGleise(idxGrenze + 1);

    this.log.debugf("  grenze=%s, totalFreigabe=%s", grenze, totalFreigabe);

    fahrstrasse.freigeben(totalFreigabe ? null : grenze);
  }

}

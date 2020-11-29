package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.FahrstrassenGleisabschnitt;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrassenelement;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.logging.Logger;

@ApplicationScoped
public class FahrstrasseMonitor {

  @Inject
  Logger log;

  @Inject
  Parcours parcours;

  /**
   * Auf Belegtänderung eines Gleisabschnitts reagieren.
   *
   * @param gleisabschnitt
   *        Gleisabschnitt
   */
  void processGleisabschnitt(@Observes Gleisabschnitt gleisabschnitt) {

    // Wenn Gleisabschnitt nicht Teil einer Fahrstrasse ist, nichts tun
    BereichselementId fahrstrasseId = gleisabschnitt.getReserviertefahrstrasseId();
    if (fahrstrasseId == null) {
      return;
    }

    Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse(fahrstrasseId.getBereich(), fahrstrasseId.getName());
    if (fahrstrasse == null) {
      return;
    }

    /*
     * Im reservierten Teil der Fahrstrasse den Gleisabschnitt suchen, der noch nicht durchfahren wurde,
     * und vor dem nur durchfahrene Gleisabschnitte liegen.
     */
    int elementAnzahl = fahrstrasse.getElemente().size();
    int idxGrenze = fahrstrasse.getTeilFreigabeAnzahl();
    Gleisabschnitt grenze = null;
    while (idxGrenze < elementAnzahl) {
      Fahrstrassenelement fe = fahrstrasse.getElemente().get(idxGrenze);
      if (fe instanceof FahrstrassenGleisabschnitt) {
        Gleisabschnitt g = ((FahrstrassenGleisabschnitt) fe).getFahrwegelement();

        if (!g.isDurchfahren()) {
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
    boolean totalFreigabe = fahrstrasse.isKomplettBesetzt(idxGrenze) || fahrstrasse.isNurGleisabschnitte(idxGrenze + 1);

    if (this.log.isDebugEnabled()) {
      this.log.debug("checkFreigabe: grenze=" + grenze + ", totalFreigabe=" + totalFreigabe);
    }

    fahrstrasse.freigeben(totalFreigabe ? null : grenze);
  }

}

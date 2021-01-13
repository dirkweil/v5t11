package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.FahrstrassenGleis;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrassenelement;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.util.cdi.Changed;
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
   * Auf Belegtänderung eines Gleiss reagieren.
   *
   * @param gleis
   *        Gleis
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

    /*
     * Im reservierten Teil der Fahrstrasse den Gleis suchen, der noch nicht durchfahren wurde,
     * und vor dem nur durchfahrene Gleise liegen.
     */
    int elementAnzahl = fahrstrasse.getElemente().size();
    int idxGrenze = fahrstrasse.getTeilFreigabeAnzahl();
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
     * - wenn ab dem ersten nicht durchfahrerenen Abschnitt alles besetzt ist
     * - oder wenn nur noch Gleise folgen.
     */
    boolean totalFreigabe = fahrstrasse.isKomplettBesetzt(idxGrenze) || fahrstrasse.isNurGleise(idxGrenze + 1);

    if (this.log.isDebugEnabled()) {
      this.log.debug("checkFreigabe: grenze=" + grenze + ", totalFreigabe=" + totalFreigabe);
    }

    fahrstrasse.freigeben(totalFreigabe ? null : grenze);
  }

}

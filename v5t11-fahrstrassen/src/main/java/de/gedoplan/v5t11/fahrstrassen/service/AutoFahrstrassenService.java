package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

@ApplicationScoped
public class AutoFahrstrassenService {

  @Inject
  Parcours parcours;

  private ListMultimap<Gleisabschnitt, Fahrstrasse> autoFahrstrassen = MultimapBuilder.hashKeys().arrayListValues().build();

  @PostConstruct
  void postConstruct() {
    // TODO Abbildung Gleisabschnitt -> Fahrstrasse neu machen
    // this.parcours.getAutoFahrstrassen().forEach(afs -> {
    // Gleisabschnitt trigger = this.parcours.getGleisabschnitt(afs.getBereich(), afs.getTrigger());
    // if (trigger != null) {
    // afs.getElemente().forEach(e -> {
    // Gleisabschnitt start = this.parcours.getGleisabschnitt(e.getStartBereich(), e.getStart());
    // if (start != null) {
    // Gleisabschnitt ende = this.parcours.getGleisabschnitt(e.getEndeBereich(), e.getEnde());
    // if (ende != null) {
    // this.parcours.getFahrstrassen(start, ende, null).forEach(fs -> this.autoFahrstrassen.put(trigger, fs));
    // }
    // }
    // });
    // }
    // });
  }

  void autoReserviere(@Observes Gleisabschnitt gleisabschnitt) {
    if (gleisabschnitt.isBesetzt()) {
      for (Fahrstrasse fs : this.autoFahrstrassen.get(gleisabschnitt)) {
        if (fs.reservieren(FahrstrassenReservierungsTyp.ZUGFAHRT, false, true)) {
          break;
        }
      }
    }
  }
}

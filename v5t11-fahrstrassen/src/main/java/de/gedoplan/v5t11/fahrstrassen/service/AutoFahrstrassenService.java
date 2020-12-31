package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.AutoFahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrstrassen.persistence.GleisabschnittRepository;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.cdi.Created;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;

import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Fahrstrassen-Automatik.
 * 
 * Ändert sich der Zustand des auslösenden Geisabschnitts ("Trigger") einer {@link AutoFahrstrasse}, wird eine der zugehörigen Fahrstrassen reserviert, soweit
 * das möglich ist.
 * 
 * @author dw
 *
 */
@ApplicationScoped
public class AutoFahrstrassenService {

  @Inject
  GleisabschnittRepository gleisabschnittRepository;

  @Inject
  Parcours parcours;

  @Inject
  Logger logger;

  @Getter(AccessLevel.PACKAGE) // nur zur Testunterstützung
  private ListMultimap<Gleisabschnitt, Fahrstrasse> autoFahrstrassen = MultimapBuilder.hashKeys().arrayListValues().build();

  @PostConstruct
  void postConstruct() {
    /*
     * AutoFahrstrasse enthält Trigger und Fahrstrassen nur in Form von Ids. Dieses Bestandteile werden zu entsprechenden Objekten
     * aufgelöst und in this.autoFahrstrassen eine Abbildung Trigger -> Liste der Fahrstrassen aufgebaut.
     */
    this.parcours.getAutoFahrstrassen().forEach(afs -> {
      BereichselementId triggerId = new BereichselementId(afs.getTriggerBereich(), afs.getTriggerName());
      Gleisabschnitt trigger = this.gleisabschnittRepository.findById(triggerId);
      if (trigger == null) {
        this.logger.warnf("Trigger nicht gefunden: %s", triggerId);
      } else {
        afs.getElemente().forEach(e -> {
          BereichselementId startId = new BereichselementId(e.getStartBereich(), e.getStartName());
          Gleisabschnitt start = this.gleisabschnittRepository.findById(startId);
          if (start == null) {
            this.logger.warnf("Start nicht gefunden: %s", startId);
          } else {
            BereichselementId endeId = new BereichselementId(e.getEndeBereich(), e.getEndeName());
            Gleisabschnitt ende = this.gleisabschnittRepository.findById(endeId);
            if (ende == null) {
              this.logger.warnf("Ende nicht gefunden: %s", endeId);
            } else {
              this.parcours.getFahrstrassen(start.getId(), ende.getId(), null).forEach(fs -> this.autoFahrstrassen.put(trigger, fs));
            }
          }
        });

        this.logger.debugf("%s -> %s", trigger.getId(), this.autoFahrstrassen.get(trigger).stream().map(Fahrstrasse::getId).collect(Collectors.toList()));
      }
    });
  }

  void startup(@Observes @Created Parcours parcours) {
  }

  /**
   * Bei Änderung eines Triggers auf besetzt, die erste zugehörige und reservierbare Fahrstrasse reservieren.
   * 
   * @param gleisabschnitt
   */
  void autoReserviere(@Observes @Changed Gleisabschnitt gleisabschnitt) {
    if (gleisabschnitt.isBesetzt()) {
      for (Fahrstrasse fs : this.autoFahrstrassen.get(gleisabschnitt)) {
        if (fs.reservieren(FahrstrassenReservierungsTyp.ZUGFAHRT, false, true)) {
          break;
        }
      }
    }
  }
}

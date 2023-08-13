package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.AutoFahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrstrassen.persistence.GleisRepository;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.cdi.Created;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;

import java.util.stream.Collectors;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.inject.Inject;

import com.google.common.collect.ListMultimap;
import com.google.common.collect.MultimapBuilder;
import org.jboss.logging.Logger;

import lombok.AccessLevel;
import lombok.Getter;

/**
 * Fahrstrassen-Automatik.
 * <p>
 * Ändert sich der Zustand des auslösenden Geisabschnitts ("Trigger") einer {@link AutoFahrstrasse}, wird eine der zugehörigen Fahrstrassen reserviert, soweit
 * das möglich ist.
 *
 * @author dw
 */
@ApplicationScoped
public class AutoFahrstrassenService {

  @Inject
  GleisRepository gleisRepository;

  @Inject
  Parcours parcours;

  @Inject
  Logger logger;

  @Getter(AccessLevel.PACKAGE) // nur zur Testunterstützung
  private ListMultimap<Gleis, Fahrstrasse> autoFahrstrassen = MultimapBuilder.hashKeys().arrayListValues().build();

  @PostConstruct
  void postConstruct() {
    /*
     * AutoFahrstrasse enthält Trigger und Fahrstrassen nur in Form von Ids. Dieses Bestandteile werden zu entsprechenden Objekten
     * aufgelöst und in this.autoFahrstrassen eine Abbildung Trigger -> Liste der Fahrstrassen aufgebaut.
     */
    this.parcours.getAutoFahrstrassen().forEach(afs -> {
      BereichselementId triggerId = new BereichselementId(afs.getTriggerBereich(), afs.getTriggerName());
      this.gleisRepository
        .findById(triggerId)
        .ifPresentOrElse(
          trigger -> {
            afs.getElemente().forEach(e -> {
              BereichselementId startId = new BereichselementId(e.getStartBereich(), e.getStartName());
              this.gleisRepository
                .findById(startId)
                .ifPresentOrElse(
                  start -> {
                    BereichselementId endeId = new BereichselementId(e.getEndeBereich(), e.getEndeName());
                    this.gleisRepository
                      .findById(endeId)
                      .ifPresentOrElse(
                        ende -> this.parcours.getFahrstrassen(start.getId(), ende.getId(), null).forEach(fs -> this.autoFahrstrassen.put(trigger, fs)),
                        () -> this.logger.warnf("Ende nicht gefunden: %s", endeId)
                      );
                  },
                  () -> this.logger.warnf("Start nicht gefunden: %s", startId)
                );
            });

            this.logger.debugf("%s -> %s", trigger.getId(), this.autoFahrstrassen.get(trigger).stream().map(Fahrstrasse::getId).collect(Collectors.toList()));
          },
          () -> this.logger.warnf("Trigger nicht gefunden: %s", triggerId));

    });
  }

  void startup(@Observes @Created Parcours parcours) {
  }

  /**
   * Bei Änderung eines Triggers auf besetzt, die erste zugehörige und reservierbare Fahrstrasse reservieren.
   *
   * @param gleis
   */
  void autoReserviere(@Observes @Changed Gleis gleis) {
    if (gleis.isBesetzt()) {
      for (Fahrstrasse fs : this.autoFahrstrassen.get(gleis)) {
        if (fs.reservieren(FahrstrassenReservierungsTyp.ZUGFAHRT, false, true)) {
          break;
        }
      }
    }
  }
}

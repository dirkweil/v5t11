package de.gedoplan.v5t11.parcours.service;

import de.gedoplan.v5t11.parcours.entity.AutoFahrstrasse;
import de.gedoplan.v5t11.parcours.entity.Parcours;
import de.gedoplan.v5t11.parcours.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.parcours.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.parcours.persistence.GleisRepository;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.cdi.Created;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
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

  // Trigger, deren Fahrstrassen in zufälliger Reihenfolge versucht werden sollen
  @Getter(AccessLevel.PACKAGE) // nur zur Testunterstützung
  private Set<Gleis> zufaelligeTrigger = new HashSet<>();

  Random random = new Random(); // nur für Tests ersetzbar (fester Seed)

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
            if (afs.isZufaellig()) {
              this.zufaelligeTrigger.add(trigger);
            }

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
   * <p>
   * Die Fahrstrassen werden in der Reihenfolge der Liste versucht, bei einer zufälligen AutoFahrstrasse in zufälliger Reihenfolge.
   *
   * @param gleis
   */
  void autoReserviere(@Observes @Changed Gleis gleis) {
    if (gleis.isBesetzt()) {
      // Kopie, da die Multimap-Liste ein Live-View ist und nicht verändert werden darf
      List<Fahrstrasse> fahrstrassen = new ArrayList<>(this.autoFahrstrassen.get(gleis));
      if (this.zufaelligeTrigger.contains(gleis)) {
        Collections.shuffle(fahrstrassen, this.random);
      }

      if (this.logger.isDebugEnabled()) {
        this.logger.debugf("AutoFahrstrassen für Trigger %s: %s", gleis.getId(), fahrstrassen.stream().map(Fahrstrasse::getId).collect(Collectors.toList()));
      }

      for (Fahrstrasse fs : fahrstrassen) {
        if (fs.reservieren(FahrstrassenReservierungsTyp.ZUGFAHRT, false, true)) {
          break;
        }
      }
    }
  }
}

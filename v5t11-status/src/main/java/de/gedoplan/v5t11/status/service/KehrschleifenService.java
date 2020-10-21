package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Schalter;
import de.gedoplan.v5t11.util.domain.attribute.SchalterStellung;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

/**
 * Q&D-Hack: Kehrschleifensteuerung.
 * 
 * TODO Dieser Hack muss in eine saubere, konfigurierbare Lösung überführt werden!
 * 
 * @author dw
 *
 */
@ApplicationScoped
public class KehrschleifenService {
  @Inject
  Log log;

  @Inject
  Steuerung steuerung;

  private Gleisabschnitt gleis109;

  private Gleisabschnitt gleis17a;

  private Gleisabschnitt gleis17b;

  private Gleisabschnitt gleis17c;

  private Gleisabschnitt gleis17d;

  private Gleisabschnitt gleis18a;

  private Gleisabschnitt gleis18b;

  private Gleisabschnitt gleis18c;

  private Set<Gleisabschnitt> gleisabschnitte = new HashSet<>();

  private Schalter kS17;

  private Schalter kS18;

  @PostConstruct
  void postConstruct() {
    try {
      this.gleis109 = this.steuerung.getGleisabschnitt("SBf", "109");
      this.gleisabschnitte.add(this.gleis109);
      this.gleis17a = this.steuerung.getGleisabschnitt("SBf", "17a");
      this.gleisabschnitte.add(this.gleis17a);
      this.gleis17b = this.steuerung.getGleisabschnitt("SBf", "17b");
      this.gleisabschnitte.add(this.gleis17b);
      this.gleis17c = this.steuerung.getGleisabschnitt("SBf", "17c");
      this.gleisabschnitte.add(this.gleis17c);
      this.gleis17d = this.steuerung.getGleisabschnitt("SBf", "17d");
      this.gleisabschnitte.add(this.gleis17d);

      this.gleis18a = this.steuerung.getGleisabschnitt("SBf", "18a");
      this.gleisabschnitte.add(this.gleis18a);
      this.gleis18b = this.steuerung.getGleisabschnitt("SBf", "18b");
      this.gleisabschnitte.add(this.gleis18b);
      this.gleis18c = this.steuerung.getGleisabschnitt("SBf", "18c");
      this.gleisabschnitte.add(this.gleis18c);

      this.kS17 = this.steuerung.getSchalter("SBf", "KS17");
      this.kS18 = this.steuerung.getSchalter("SBf", "KS18");
    } catch (Exception ignore) {
    }
  }

  void gleisChanged(@ObservesAsync Gleisabschnitt gleisabschnitt) {
    if (this.gleisabschnitte.contains(gleisabschnitt)) {

      // Handling für KS17
      if (this.gleis17c.isBesetzt() || this.gleis17d.isBesetzt()) {
        this.log.debug("Polung KS17: links");
        this.kS17.setStellung(SchalterStellung.EIN);
      } else if (this.gleis109.isBesetzt() || this.gleis17a.isBesetzt()) {
        this.log.debug("Polung KS17: rechts");
        this.kS17.setStellung(SchalterStellung.AUS);
      }

      // Handling für KS18
      if (this.gleis18c.isBesetzt()) {
        this.log.debug("Polung KS18: links");
        this.kS18.setStellung(SchalterStellung.EIN);
      } else if (this.gleis18a.isBesetzt()) {
        this.log.debug("Polung KS18: rechts");
        this.kS18.setStellung(SchalterStellung.AUS);
      }

    }
  }
}

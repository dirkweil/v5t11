package de.gedoplan.v5t11.status.service;

import org.jboss.logging.Logger;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Connected;
import de.gedoplan.v5t11.status.entity.baustein.Disconnected;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeugdecoder;
import de.gedoplan.v5t11.status.persistence.FahrzeugdecoderRepository;
import de.gedoplan.v5t11.status.persistence.WeicheRepository;
import de.gedoplan.v5t11.util.misc.Delay;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class AnlagenstatusService {

  @Inject
  Steuerung steuerung;

  @Inject
  AutoSkriptService autoSkriptService;

  @Inject
  Logger logger;

  @Inject
  WeicheRepository weicheRepository;

  @Inject
  FahrzeugdecoderRepository fahrzeugdecoderRepository;

  public void init() {
  }

  void onConnect(@ObservesAsync @Connected Zentrale zentrale) {
    this.logger.debug("***** Connected *****");
    // Gleisprotokoll (z. B. SX1+SX2+DCC) einstellen
    zentrale.setGleisProtokoll();

    // Weichenstellungen wiederherstellen
    this.logger.debug("Nicht stellungssichere Weichen auf letzte bekannte Stellung stellen");
    this.weicheRepository
        .findAll()
        .forEach(w -> {
          Weiche weiche = this.steuerung.getWeiche(w.getBereich(), w.getName());
          if (weiche != null) {
            if (!weiche.isStellungsSicher()) {
              this.logger.debugf("  %s %s", weiche.toString(true), w.getStellung());
              weiche.setStellung(w.getStellung().getAndereStellung());
              delay();
              weiche.setStellung(w.getStellung());
              delay();
            }
          }
        });

    // Alle Autoskripte einmal ausführen
    this.autoSkriptService.executeAll();

    // Fahrzeugdecoder wiederherstellen
    this.logger.debug("Fahrzeugdecoder auf letzten bekannten Stand bringen (aber Fahrstufe 0)");
    this.fahrzeugdecoderRepository
        .findAll()
        .forEach(fzd -> {
          Fahrzeugdecoder fahrzeugdecoder = this.steuerung.getOrCreateFahrzeugdecoder(fzd.getId());
          fahrzeugdecoder.setAktiv(fzd.isAktiv());
          fahrzeugdecoder.setFktBits(fzd.getFktBits());
          fahrzeugdecoder.setLicht(fzd.isLicht());
          fahrzeugdecoder.setRueckwaerts(fzd.isRueckwaerts());
          this.logger.debugf("  %s", fahrzeugdecoder);
        });
  }

  private void delay() {
    if (this.steuerung.getZentrale().isEchtbetrieb()) {
      Delay.delay(250);
    }
  }

  @Transactional
  void onDisconnect(@ObservesAsync @Disconnected Zentrale zentrale) {
    this.logger.debug("***** Disconnected *****");

    this.logger.debug("Stellungen der nicht stellungssicheren Weichen speichern");
    this.steuerung
        .getWeichen()
        .stream()
        .filter(w -> !w.isStellungsSicher())
        .forEach(w -> {
          this.logger.debugf("  %s %s", w.toString(true), w.getStellung());
          this.weicheRepository.merge(w);
        });

    this.logger.debug("Zustand der bekannten Fahrzeugdecoder speichern");
    this.fahrzeugdecoderRepository.removeAll();
    this.steuerung
        .getFahrzeugdecoder()
        .forEach(fzd -> {
          this.logger.debugf("  %s", fzd);
          this.fahrzeugdecoderRepository.persist(fzd);
        });
  }

}

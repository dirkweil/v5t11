package de.gedoplan.v5t11.status.service;

import org.jboss.logging.Logger;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Connected;
import de.gedoplan.v5t11.status.entity.baustein.Disconnected;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
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
              this.logger.debugf("%s -> %s", weiche.toString(true), w.getStellung());
              weiche.setStellung(w.getStellung().getAndereStellung());
              Delay.delay(250);
              weiche.setStellung(w.getStellung());
              Delay.delay(250);
            }

          }
        });

    // Alle Autoskripte einmal ausführen
    this.autoSkriptService.executeAll();
  }

  @Transactional
  void onDisconnect(@ObservesAsync @Disconnected Zentrale zentrale) {
    this.logger.debug("***** Disconnected *****");

    this.steuerung
        .getWeichen()
        .forEach(w -> {
          this.logger.debugf("Weichenstellung merken: %s %s", w.toString(true), w.getStellung());
          this.weicheRepository.merge(w);
        });
  }

}

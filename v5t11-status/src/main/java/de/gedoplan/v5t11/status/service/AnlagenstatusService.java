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

    zentrale.initialize();

    // Weichenstellungen wiederherstellen (erst andere Stellung, dann richtige Stellung)
    this.logger.debug("Nicht stellungssichere Weichen auf letzte bekannte Stellung stellen");
    try {
      this.weicheRepository
          .findAll()
          .forEach(w -> {
            Weiche weiche = this.steuerung.getWeiche(w.getBereich(), w.getName());
            if (weiche != null) {
              if (!weiche.isStellungsSicher()) {
                this.logger.debugf("  %s %s", weiche.toString(true), w.getStellung());
                weiche.setStellung(w.getStellung().getAndereStellung());
                delay();
              }
            }
          });
      this.weicheRepository
          .findAll()
          .forEach(w -> {
            Weiche weiche = this.steuerung.getWeiche(w.getBereich(), w.getName());
            if (weiche != null) {
              if (!weiche.isStellungsSicher()) {
                weiche.setStellung(w.getStellung());
                delay();
              }
            }
          });
    } catch (Exception e) {
      this.logger.error("Fehler beim Weichenstellen", e);
    }

    // Alle Autoskripte einmal ausführen
    try {
      this.autoSkriptService.executeAll();
    } catch (Exception e) {
      this.logger.error("Fehler beim Ausführen der Autoskripte", e);
    }

    // Fahrzeugdecoder wiederherstellen
    this.logger.debug("Fahrzeugdecoder auf letzten bekannten Stand bringen (aber Fahrstufe 0)");
    try {
      this.fahrzeugdecoderRepository
          .findAll()
          .stream()
          .filter(fzd -> fzd.getDecoderAdr().isAdresseValid())
          .forEach(fzd -> {
            Fahrzeugdecoder fahrzeugdecoder = this.steuerung.getOrCreateFahrzeugdecoder(fzd.getId());
            fahrzeugdecoder.setAktiv(fzd.isAktiv());
            fahrzeugdecoder.setFktBits(fzd.getFktBits());
            fahrzeugdecoder.setLicht(fzd.isLicht());
            fahrzeugdecoder.setRueckwaerts(fzd.isRueckwaerts());
            this.logger.debugf("  %s", fahrzeugdecoder);

            // Falls nötig, Decoder in Zentrale an/abmelden
            this.steuerung.getZentrale().decoderChanged(fahrzeugdecoder);
          });
    } catch (Exception e) {
      this.logger.error("Fehler beim Setzen der Fahrzeugdecoder", e);
    }
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
    try {
      this.steuerung
          .getWeichen()
          .stream()
          .filter(w -> !w.isStellungsSicher())
          .forEach(w -> {
            this.logger.debugf("  %s %s", w.toString(true), w.getStellung());
            this.weicheRepository.merge(w);
          });
    } catch (Exception e) {
      this.logger.error("Fehler beim Speichern der Weichen", e);
    }

    this.logger.debug("Zustand der bekannten Fahrzeugdecoder speichern");
    try {
      this.fahrzeugdecoderRepository.removeAll();
      this.steuerung
          .getFahrzeugdecoder()
          .stream()
          .filter(fzd -> fzd.getDecoderAdr().isAdresseValid())
          .forEach(fzd -> {
            this.logger.debugf("  %s", fzd);
            this.fahrzeugdecoderRepository.persist(fzd);
          });
    } catch (Exception e) {
      this.logger.error("Fehler beim Speichern der Fahrzeugdecoder", e);
    }
  }

}

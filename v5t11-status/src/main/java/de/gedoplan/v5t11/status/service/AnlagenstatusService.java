package de.gedoplan.v5t11.status.service;

import org.jboss.logging.Logger;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Connected;
import de.gedoplan.v5t11.status.entity.baustein.Disconnected;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeugdecoder;
import de.gedoplan.v5t11.status.persistence.FahrzeugdecoderRepository;
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
  FahrzeugdecoderRepository fahrzeugdecoderRepository;

  private volatile boolean shuttingDown;

  public void init() {
  }

  void onConnect(@ObservesAsync @Connected Zentrale zentrale) {
    this.logger.debug("***** Connected *****");

    zentrale.initialize();

    // Magnetartikel-Weichen gelten als nicht gesichert, bis sie das erste Mal gestellt werden
    this.steuerung.getWeichen().forEach(Weiche::resetStellungGesichert);

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

  /**
   * Zustand speichern, bevor beim Herunterfahren die Verbindung zur Zentrale geschlossen wird.
   * <p>
   * Der asynchrone {@link #onDisconnect}-Observer käme zu spät, da dann ggf. schon der EntityManager geschlossen ist; er wird
   * daher nach dem Aufruf dieser Methode übersprungen.
   */
  @Transactional
  public void saveStateOnShutdown() {
    this.shuttingDown = true;
    saveState();
  }

  @Transactional
  void onDisconnect(@ObservesAsync @Disconnected Zentrale zentrale) {
    this.steuerung.getWeichen().forEach(Weiche::resetStellungGesichert);

    if (this.shuttingDown) {
      this.logger.debug("***** Disconnected (Shutdown, Zustand bereits gespeichert) *****");
      return;
    }

    this.logger.debug("***** Disconnected *****");
    saveState();
  }

  private void saveState() {
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

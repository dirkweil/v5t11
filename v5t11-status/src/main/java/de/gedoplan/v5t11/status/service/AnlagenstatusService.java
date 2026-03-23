package de.gedoplan.v5t11.status.service;

import org.jboss.logging.Logger;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Connected;
import de.gedoplan.v5t11.status.entity.baustein.Disconnected;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;

@ApplicationScoped
public class AnlagenstatusService {

  @Inject
  Steuerung steuerung;

  @Inject
  AutoSkriptService autoSkriptService;

  @Inject
  Logger logger;

  public void init() {
    this.logger.debug("Initiale Kanalwerte aus DB holen (nicht mehr ...)");
  }

  void onConnect(@ObservesAsync @Connected Zentrale zentrale) {
    this.logger.debug("***** Connected *****");
    // Gleisprotokoll (z. B. SX1+SX2+DCC) einstellen
    zentrale.setGleisProtokoll();

    // Alle Autoskripte einmal ausführen
    this.autoSkriptService.executeAll();
  }

  void onDisconnect(@ObservesAsync @Disconnected Zentrale zentrale) {
    this.logger.debug("***** Disconnected *****");

    this.steuerung
        .getWeichen()
        .forEach(w -> this.logger.debugf("Weichenstellung merken: %s %s", w.toString(true), w.getStellung()));
  }

}

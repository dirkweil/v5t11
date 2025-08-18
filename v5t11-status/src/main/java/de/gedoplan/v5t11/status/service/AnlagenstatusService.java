package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Kanal;
import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Connected;
import de.gedoplan.v5t11.status.entity.baustein.Disconnected;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.persistence.KanalRepository;
import de.gedoplan.v5t11.util.cdi.Changed;

import java.util.HashMap;
import java.util.Map;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.ObservesAsync;
import jakarta.inject.Inject;

import org.jboss.logging.Logger;

@ApplicationScoped
public class AnlagenstatusService {

  @Inject
  Steuerung steuerung;

  @Inject
  AutoSkriptService autoSkriptService;

  @Inject
  KanalRepository kanalRepository;

  @Inject
  Logger logger;

  private Map<Integer, Integer> initialeKanalwerte = new HashMap<>();

  public void init() {
    this.logger.debug("Initiale Kanalwerte aus DB holen");
    this.kanalRepository
      .findAll()
      .stream()
      .peek(logger::debug)
      .forEach(k -> initialeKanalwerte.put(k.getAdresse(), k.getWert()));
  }

  void onConnect(@ObservesAsync @Connected Zentrale zentrale) {
    this.logger.debug("***** Connected *****");
    // Gleisprotokoll (z. B. SX1+SX2+DCC) einstellen
    zentrale.setGleisProtokoll();

    // Alle Bausteine auf den uns bekannten Status setzen
    //    this.steuerung.getBausteinAdressen().forEach(adr -> zentrale.setSX1Kanal(adr, this.initialeKanalwerte.get(adr)));

    // Alle Autoskripte einmal ausführen
    this.autoSkriptService.executeAll();
  }

  void onDisconnect(@ObservesAsync @Disconnected Zentrale zentrale) {
    this.logger.debug("***** Disconnected *****");
  }

  void onKanalChange(@ObservesAsync @Changed Kanal kanal) {
    this.kanalRepository.merge(kanal);
  }

}

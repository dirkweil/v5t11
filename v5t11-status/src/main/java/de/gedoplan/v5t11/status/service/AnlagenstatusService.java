package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Kanal;
import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Connected;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.persistence.KanalRepository;
import de.gedoplan.v5t11.util.cdi.Changed;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

@ApplicationScoped
public class AnlagenstatusService {

  @Inject
  Steuerung steuerung;

  @Inject
  AutoSkriptService autoSkriptService;

  @Inject
  KanalRepository kanalRepository;

  public void init() {
    this.kanalRepository.findAll().forEach(k -> this.steuerung.setSX1Kanal(k.getAdresse(), k.getWert()));
  }

  void onConnect(@Observes @Connected Zentrale zentrale) {
    // Gleisprotokoll (z. B. SX1+SX2+DCC) einstellen
    zentrale.setGleisProtokoll();

    // Alle Bausteine auf den uns bekannten Status setzen
    this.steuerung.getBausteinAdressen().forEach(adr -> zentrale.setSX1Kanal(adr, this.steuerung.getSX1Kanal(adr)));

    // Alle Autoskripte einmal ausführen
    this.autoSkriptService.executeAll();
  }

  void onKanalChange(@ObservesAsync @Changed Kanal kanal) {
    this.kanalRepository.merge(kanal);
  }

}

package de.gedoplan.v5t11.leitstand.service;

import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.leitstand.gateway.FahrstrassenGateway;
import de.gedoplan.v5t11.leitstand.persistence.FahrstrasseRepository;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class FahrstrassenManager {

  @Inject
  @RestClient
  FahrstrassenGateway fahrstrassenGateway;

  @Inject
  FahrstrasseRepository fahrstrasseRepository;

  @Inject
  Logger logger;

  @Inject
  EventFirer eventFirer;

  @Transactional(rollbackOn = Exception.class)
  public void updateFahrstrasse(Fahrstrasse statusFahrstrasse) {
    this.logger.debugf("Update für Fahrstrasse: %s", statusFahrstrasse);

    Fahrstrasse fahrstrasse = this.fahrstrasseRepository.findById(statusFahrstrasse.getId()).orElse(null);
    if (fahrstrasse == null) {
      // Fahrstrasse ist noch nicht hier registriert
      this.logger.debugf("Fahrstrasse ist nicht registriert");

      // Falls UNRESERVIERT, ist nicht zu tun
      if (statusFahrstrasse.getReservierungsTyp() == FahrstrassenReservierungsTyp.UNRESERVIERT) {
        this.logger.debugf("UNRESERVIERT -> nix zu tun");
        return;
      }

      // Ansonsten Fahrstrassendaten komplett (inkl. FS-Elementen) holen und hier registrieren
      try {
        fahrstrasse = this.fahrstrassenGateway.getFahrstrasse(statusFahrstrasse.getId());
      } catch (NotFoundException nfe) {
        this.logger.warnf("Fahrstrasse nicht gefunden: %s", statusFahrstrasse.getId());
        return;
      }

      this.logger.debugf("Fahrstrasse registrieren: %s", fahrstrasse);
      this.fahrstrasseRepository.persist(fahrstrasse);
    } else {
      // Fahrstrasse ist bereits hier registriert
      this.logger.debugf("Fahrstrasse ist bereits registiert");

      // Daten aktualisieren
      fahrstrasse.setReservierungsTyp(statusFahrstrasse.getReservierungsTyp());
      fahrstrasse.setTeilFreigabeAnzahl(statusFahrstrasse.getTeilFreigabeAnzahl());

      // Falls UNRESERVIERT, hier entfernen
      if (fahrstrasse.getReservierungsTyp() == FahrstrassenReservierungsTyp.UNRESERVIERT) {
        this.logger.debugf("UNRESERVIERT -> deregistrieren");
        this.fahrstrasseRepository.removeById(statusFahrstrasse.getId());
      } else {
        this.logger.debugf("Fahrstrasse aktualisieren: %s", fahrstrasse);
      }
    }

    this.eventFirer.fire(fahrstrasse, Changed.Literal.INSTANCE);
  }

  public Fahrstrasse getReservierteFahrstrasse(Gleis gleis) {
    return this.fahrstrasseRepository
      .findByGleis(gleis)
      .stream()
      .filter(fs -> fs.getElement(gleis, true) != null)
      .findAny()
      .orElse(null);
  }
}

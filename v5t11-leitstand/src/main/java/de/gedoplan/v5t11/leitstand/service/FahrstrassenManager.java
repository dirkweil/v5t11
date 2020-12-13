package de.gedoplan.v5t11.leitstand.service;

import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.leitstand.gateway.FahrstrassenGateway;
import de.gedoplan.v5t11.leitstand.persistence.FahrstrasseRepository;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;

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
  Logger log;

  @Inject
  EventFirer eventFirer;

  @Transactional(rollbackOn = Exception.class)
  public void updateFahrstrasse(Fahrstrasse statusFahrstrasse) {
    Fahrstrasse fahrstrasse = this.fahrstrasseRepository.findById(statusFahrstrasse.getId());
    if (fahrstrasse == null) {
      // Fahrstrasse ist noch nicht hier registriert

      // Falls UNRESERVIERT, ist nicht zu tun
      if (statusFahrstrasse.getReservierungsTyp() == FahrstrassenReservierungsTyp.UNRESERVIERT) {
        return;
      }

      // Ansonsten Fahrstrassendaten komplett holen und hier registrieren
      try {
        fahrstrasse = this.fahrstrassenGateway.getFahrstrasse(statusFahrstrasse.getBereich(), statusFahrstrasse.getName());
      } catch (NotFoundException nfe) {
        this.log.warn("Fahrstrasse nicht gefunden: " + statusFahrstrasse.getBereich() + "/" + statusFahrstrasse.getName());
        return;
      }
      this.fahrstrasseRepository.persist(fahrstrasse);
    } else {
      // Fahrstrasse ist bereits hier registriert

      // Falls UNRESERVIERT, hier entfernen
      if (statusFahrstrasse.getReservierungsTyp() == FahrstrassenReservierungsTyp.UNRESERVIERT) {
        this.fahrstrasseRepository.removeById(statusFahrstrasse.getId());
      } else {
        // Daten aktualisieren
        fahrstrasse.setReservierungsTyp(statusFahrstrasse.getReservierungsTyp());
        fahrstrasse.setTeilFreigabeAnzahl(statusFahrstrasse.getTeilFreigabeAnzahl());
      }
    }

    this.eventFirer.fire(fahrstrasse);
  }

  public Fahrstrasse getReservierteFahrstrasse(Gleisabschnitt gleisabschnitt) {
    return this.fahrstrasseRepository
        .findByGleisabschnitt(gleisabschnitt)
        .stream()
        .filter(fs -> fs.getElement(gleisabschnitt, true) != null)
        .findAny()
        .orElse(null);
  }
}

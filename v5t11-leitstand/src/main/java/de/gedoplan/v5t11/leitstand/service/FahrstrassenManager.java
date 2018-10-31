package de.gedoplan.v5t11.leitstand.service;

import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.leitstand.gateway.FahrstrasseResourceClient;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenFilter;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.domain.entity.Fahrwegelement;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.NotFoundException;

import org.apache.commons.logging.Log;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;

@ApplicationScoped
public class FahrstrassenManager {

  @Inject
  FahrstrasseResourceClient fahrstrasseResourceClient;

  @Inject
  Log log;

  private Table<String, String, Fahrstrasse> aktiveFahrstrassen = HashBasedTable.create();

  @PostConstruct
  void postConstruct() {
    try {
      this.fahrstrasseResourceClient.getFahrstrassen(
          null, null,
          null, null,
          FahrstrassenFilter.RESERVIERT)
          .forEach(fs -> this.aktiveFahrstrassen.put(fs.getBereich(), fs.getName(), fs));
    } catch (Exception e) {
      this.log.warn("Kann aktive Fahrstrassen nicht ermitteln", e);
    }
  }

  public Fahrstrasse updateFahrstrasse(Fahrstrasse statusFahrstrasse) {
    Fahrstrasse fahrstrasse = this.aktiveFahrstrassen.get(statusFahrstrasse.getBereich(), statusFahrstrasse.getName());
    if (fahrstrasse == null) {
      // Fahrstrasse ist noch nicht hier registriert

      // Falls UNRESERVIERT, ist nicht zu tun
      if (statusFahrstrasse.getReservierungsTyp() == FahrstrassenReservierungsTyp.UNRESERVIERT) {
        return null;
      }

      // Ansonsten Fahrstrassendaten komplett holen und hier registrieren
      try {
        fahrstrasse = this.fahrstrasseResourceClient.getFahrstrasse(statusFahrstrasse.getBereich(), statusFahrstrasse.getName());
      } catch (NotFoundException nfe) {
        this.log.warn("Fahrstrasse nicht gefunden: " + statusFahrstrasse.getBereich() + "/" + statusFahrstrasse.getName());
        return null;
      }
      this.aktiveFahrstrassen.put(statusFahrstrasse.getBereich(), statusFahrstrasse.getName(), fahrstrasse);
    } else {
      // Fahrstrasse ist bereits hier registriert

      // Daten aktualisieren
      fahrstrasse.setReservierungsTyp(statusFahrstrasse.getReservierungsTyp());
      fahrstrasse.setTeilFreigabeAnzahl(statusFahrstrasse.getTeilFreigabeAnzahl());

      // Falls UNRESERVIERT, hier entfernen
      if (statusFahrstrasse.getReservierungsTyp() == FahrstrassenReservierungsTyp.UNRESERVIERT) {
        this.aktiveFahrstrassen.remove(statusFahrstrasse.getBereich(), statusFahrstrasse.getName());
      }
    }

    return fahrstrasse;
  }

  // TODO Werte cachen?
  public Fahrstrasse getReservierteFahrstrasse(Fahrwegelement fahrwegelement) {
    for (Fahrstrasse fahrstrasse : this.aktiveFahrstrassen.values()) {
      if (fahrstrasse.getElement(fahrwegelement, true) != null) {
        return fahrstrasse;
      }
    }

    return null;
  }

  public FahrstrassenReservierungsTyp getGleisabschnittReservierung(Gleisabschnitt gleisabschnitt) {
    Fahrstrasse fahrstrasse = getReservierteFahrstrasse(gleisabschnitt);
    return fahrstrasse != null ? fahrstrasse.getReservierungsTyp() : FahrstrassenReservierungsTyp.UNRESERVIERT;
  }

}

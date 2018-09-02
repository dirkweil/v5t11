package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.fahrstrassen.gateway.GleisResourceClient;
import de.gedoplan.v5t11.fahrstrassen.gateway.SignalResourceClient;
import de.gedoplan.v5t11.fahrstrassen.gateway.WeicheResourceClient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class ParcoursProducer {

  @Produces
  @ApplicationScoped
  Parcours createParcours(ConfigService configService, GleisResourceClient gleisResourceClient, SignalResourceClient signalResourceClient, WeicheResourceClient weicheResourceClient) {

    Parcours parcours = configService.readXmlConfig("_parcours.xml", Parcours.class);

    // Aktuelle Zustände von Gleisabschnitten, Weichen und Signalen holen
    gleisResourceClient.getGleisabschnitte().forEach(other -> {
      Gleisabschnitt gleisabschnitt = parcours.getGleisabschnitt(other.getBereich(), other.getName());
      if (gleisabschnitt != null) {
        gleisabschnitt.copyStatus(other);
      }
    });
    signalResourceClient.getSignale().forEach(other -> {
      Signal signal = parcours.getSignal(other.getBereich(), other.getName());
      if (signal != null) {
        signal.copyStatus(other);
      }
    });
    weicheResourceClient.getWeichen().forEach(other -> {
      Weiche weiche = parcours.getWeiche(other.getBereich(), other.getName());
      if (weiche != null) {
        weiche.copyStatus(other);
      }
    });

    // Fahrstrassen komplettieren
    parcours.completeFahrstrassen();

    return parcours;
  }

}

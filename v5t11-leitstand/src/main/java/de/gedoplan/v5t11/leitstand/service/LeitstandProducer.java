package de.gedoplan.v5t11.leitstand.service;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.gateway.GleisResourceClient;
import de.gedoplan.v5t11.leitstand.gateway.SignalResourceClient;
import de.gedoplan.v5t11.leitstand.gateway.WeicheResourceClient;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class LeitstandProducer {

  @Produces
  @ApplicationScoped
  Leitstand createLeitstand(ConfigService configService, GleisResourceClient gleisResourceClient, SignalResourceClient signalResourceClient, WeicheResourceClient weicheResourceClient) {

    Leitstand leitstand = configService.readXmlConfig("_leitstand.xml", Leitstand.class);

    // Aktuelle Zustände von Gleisabschnitten, Weichen und Signalen holen
    gleisResourceClient.getGleisabschnitte().forEach(other -> {
      Gleisabschnitt gleisabschnitt = leitstand.getGleisabschnitt(other.getBereich(), other.getName());
      if (gleisabschnitt != null) {
        gleisabschnitt.copyStatus(other);
      }
    });
    signalResourceClient.getSignale().forEach(other -> {
      Signal signal = leitstand.getSignal(other.getBereich(), other.getName());
      if (signal != null) {
        signal.copyStatus(other);
      }
    });
    weicheResourceClient.getWeichen().forEach(other -> {
      Weiche weiche = leitstand.getWeiche(other.getBereich(), other.getName());
      if (weiche != null) {
        weiche.copyStatus(other);
      }
    });

    return leitstand;
  }

}

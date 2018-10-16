package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.gateway.GleisResourceClient;
import de.gedoplan.v5t11.fahrstrassen.gateway.SignalResourceClient;
import de.gedoplan.v5t11.fahrstrassen.gateway.WeicheResourceClient;
import de.gedoplan.v5t11.util.cdi.Created;
import de.gedoplan.v5t11.util.cdi.EventFirer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class ParcoursProducer {

  @Produces
  @ApplicationScoped
  Parcours createParcours(ConfigService configService, GleisResourceClient gleisResourceClient, SignalResourceClient signalResourceClient, WeicheResourceClient weicheResourceClient) {

    Parcours parcours = configService.readXmlConfig("_parcours.xml", Parcours.class);

    // Fahrstrassen komplettieren
    parcours.completeFahrstrassen();

    EventFirer.getInstance().fire(parcours, Created.Literal.INSTANCE);

    return parcours;
  }

}

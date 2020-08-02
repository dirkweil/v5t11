package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class ParcoursProducer {

  @Produces
  @ApplicationScoped
  Parcours createParcours(ConfigService configService) {

    Parcours parcours = configService.readXmlConfig("_parcours.xml", Parcours.class);

    // Fahrstrassen komplettieren
    parcours.completeFahrstrassen();

    // TODO
    // EventFirer.getInstance().fire(parcours, Created.Literal.INSTANCE);

    return parcours;
  }

}

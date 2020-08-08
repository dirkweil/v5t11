package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.util.cdi.Created;
import de.gedoplan.v5t11.util.cdi.EventFirer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class ParcoursProducer {

  @Inject
  EventFirer eventFirer;

  @Produces
  @ApplicationScoped
  Parcours createParcours(ConfigService configService) {

    Parcours parcours = configService.readXmlConfig("_parcours.xml", Parcours.class);

    // Fahrstrassen komplettieren
    parcours.completeFahrstrassen();

    parcours.injectFields();

    this.eventFirer.fire(parcours, Created.Literal.INSTANCE);

    return parcours;
  }

}

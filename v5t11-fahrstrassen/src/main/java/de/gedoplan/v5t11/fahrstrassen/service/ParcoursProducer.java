package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.persistence.GleisabschnittRepository;
import de.gedoplan.v5t11.util.cdi.Created;
import de.gedoplan.v5t11.util.cdi.EventFirer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.jboss.logging.Logger;

@ApplicationScoped
public class ParcoursProducer {

  @Inject
  GleisabschnittRepository parcoursRepository;

  @Inject
  Logger logger;

  @Inject
  EventFirer eventFirer;

  @Produces
  @ApplicationScoped
  @Transactional(rollbackOn = Exception.class)
  Parcours createParcours(ConfigService configService) {

    if (this.logger.isDebugEnabled()) {
      this.logger.debug("Parcours aus XML lesen");
    }

    // Parcours aus XML lesen
    Parcours parcours = configService.readXmlConfig("_parcours.xml", Parcours.class);
    parcours.injectFields();
    parcours.addPersistentEntries();
    parcours.completeFahrstrassen();
    parcours.injectFields();
    parcours.addPersistentEntries();

    this.eventFirer.fire(parcours, Created.Literal.INSTANCE);

    return parcours;
  }

}

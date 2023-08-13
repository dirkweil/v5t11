package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.persistence.GleisRepository;
import de.gedoplan.v5t11.util.cdi.EventFirer;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import org.jboss.logging.Logger;

@ApplicationScoped
public class ParcoursProducer {

  @Inject
  GleisRepository parcoursRepository;

  @Inject
  VorsignalService vorsignalService;

  @Inject
  Logger logger;

  @Inject
  EventFirer eventFirer;

  @Produces
  @ApplicationScoped
  @Transactional(rollbackOn = Exception.class)
  Parcours createParcours(ConfigService configService) {

    this.logger.debugf("Parcours aus XML lesen");

    // Parcours aus XML lesen
    Parcours parcours = configService.readXmlConfig("_parcours.xml", Parcours.class);
    parcours.injectFields();
    parcours.addPersistentEntries();

    parcours.completeFahrstrassen();
    parcours.removeUnerlaubteFahrstrassen();
    parcours.adjustLangsamfahrt();
    parcours.injectFields();
    parcours.addPersistentEntries();

    return parcours;
  }

}

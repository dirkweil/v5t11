package de.gedoplan.v5t11.parcours.service;

import de.gedoplan.v5t11.parcours.entity.Parcours;
import de.gedoplan.v5t11.parcours.persistence.GleisRepository;
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
    this.logger.infof("a) %d", parcours.getFahrstrassen().size());
    this.logger.infof("b) %d", parcours.getFahrstrassen().stream().filter(fs -> !fs.isCombi()).count());
    this.logger.infof("c) %d", parcours.getFahrstrassen().stream().filter(fs -> fs.isUmkehrbar()).count());
    parcours.injectFields();
    parcours.addPersistentEntries();

    parcours.completeFahrstrassen();
    this.logger.infof("d) %d", parcours.getFahrstrassen().stream().filter(fs -> !fs.isCombi()).count());
    parcours.removeUnerlaubteFahrstrassen();
    this.logger.infof("e) %d", parcours.getFahrstrassen().stream().filter(fs -> !fs.isCombi()).count());
    parcours.adjustLangsamfahrt();
    this.logger.infof("f) %d", parcours.getFahrstrassen().stream().filter(fs -> !fs.isCombi()).count());
    parcours.injectFields();
    parcours.addPersistentEntries();
    this.logger.infof("z) %d", parcours.getFahrstrassen().stream().filter(fs -> !fs.isCombi()).count());

    return parcours;
  }

}

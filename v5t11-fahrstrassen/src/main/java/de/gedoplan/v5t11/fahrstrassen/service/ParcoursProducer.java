package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.persistence.ParcoursRepository;
import de.gedoplan.v5t11.util.cdi.Created;
import de.gedoplan.v5t11.util.cdi.EventFirer;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.jboss.logging.Logger;

@ApplicationScoped
public class ParcoursProducer {

  @Inject
  ParcoursRepository parcoursRepository;

  @Inject
  EntityManager entityManager;

  @Inject
  Logger logger;

  @Inject
  EventFirer eventFirer;

  @Produces
  @ApplicationScoped
  Parcours createParcours(ConfigService configService) {

    long xmlConfigLastModified = configService.getXmlConfigLastModified("_parcours.xml");
    boolean reRead = true;
    Parcours parcours = this.parcoursRepository.findById(configService.getAnlage());
    if (parcours != null) {
      parcours.injectFields();
      reRead = xmlConfigLastModified > parcours.getLastModified();
      if (this.logger.isDebugEnabled()) {
        this.logger.debug(String.format("lastModified: db=%tF %<tT, xml=%tF %<tT ==> reRead=%b", parcours.getLastModified(), xmlConfigLastModified, reRead));
      }
    }

    if (reRead) {
      if (this.logger.isDebugEnabled()) {
        this.logger.debug("Parcours aus XML lesen");
      }

      if (parcours != null) {
        this.parcoursRepository.remove(parcours);
      }

      parcours = configService.readXmlConfig("_parcours.xml", Parcours.class);
      parcours.injectFields();
      parcours.setId(configService.getAnlage());
      parcours.setLastModified(xmlConfigLastModified);
      parcours.completeFahrstrassen();

      this.parcoursRepository.persist(parcours);
    }

    this.eventFirer.fire(parcours, Created.Literal.INSTANCE);

    return parcours;
  }

}

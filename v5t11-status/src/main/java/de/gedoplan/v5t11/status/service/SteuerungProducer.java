package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Steuerung;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

import org.jboss.logging.Logger;

@ApplicationScoped
public class SteuerungProducer {

  @Produces
  @ApplicationScoped
  Steuerung createSteuerung(ConfigService configService, Logger logger) {

    Steuerung steuerung = configService.readXmlConfig("_sx.xml", Steuerung.class);
    steuerung.injectFields();
    steuerung.postConstruct();

    logger.debugf("Produced steuerung for anlage %s", configService.getAnlage());

    return steuerung;
  }

}

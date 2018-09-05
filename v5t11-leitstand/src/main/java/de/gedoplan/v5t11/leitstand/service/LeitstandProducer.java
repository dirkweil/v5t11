package de.gedoplan.v5t11.leitstand.service;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class LeitstandProducer {

  @Produces
  @ApplicationScoped
  Leitstand createLeitstand(ConfigService configService) {

    Leitstand leitstand = configService.readXmlConfig("_leitstand.xml", Leitstand.class);

    return leitstand;
  }

}

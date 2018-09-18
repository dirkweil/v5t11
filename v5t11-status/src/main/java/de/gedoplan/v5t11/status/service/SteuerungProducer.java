package de.gedoplan.v5t11.status.service;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.status.entity.Steuerung;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class SteuerungProducer {

  @Produces
  @ApplicationScoped
  Steuerung createSteuerung(ConfigService configService) {

    Steuerung steuerung = configService.readXmlConfig("_sx.xml", Steuerung.class);

    InjectionUtil.injectFields(steuerung);

    return steuerung;
  }

}
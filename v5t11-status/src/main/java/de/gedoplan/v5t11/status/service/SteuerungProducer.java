package de.gedoplan.v5t11.status.service;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.util.config.ConfigUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class SteuerungProducer {

  @Produces
  @ApplicationScoped
  Steuerung createSteuerung() {

    Steuerung steuerung = ConfigUtil.readXmlConfig("_sx.xml", Steuerung.class);

    InjectionUtil.injectFields(steuerung);

    return steuerung;
  }

}

package de.gedoplan.v5t11.status.service;

import de.gedoplan.baselibs.utils.xml.XmlConverter;
import de.gedoplan.v5t11.status.entity.Steuerung;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.CreationException;
import javax.enterprise.inject.Produces;

@ApplicationScoped
public class SteuerungProducer {

  @Produces
  @ApplicationScoped
  Steuerung createSteuerung() {

    String xmlResourceName = "defaultSx.xml";
    try {
      return XmlConverter.fromXml(Steuerung.class, xmlResourceName);
    } catch (Exception e) {
      throw new CreationException("Cannot read " + xmlResourceName, e);
    }
  }

}

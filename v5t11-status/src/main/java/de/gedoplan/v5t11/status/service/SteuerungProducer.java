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

    // TODO Dateinamen konfigurierbar machen
    String xmlResourceName = "testSx.xml";
    try {
      return XmlConverter.fromXml(Steuerung.class, xmlResourceName);
    } catch (Exception e) {
      throw new CreationException("Cannot read " + xmlResourceName, e);
    }
  }

}

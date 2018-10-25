package de.gedoplan.v5t11.betriebssteuerung.producer;

import de.gedoplan.baselibs.utils.xml.XmlConverter;
import de.gedoplan.v5t11.betriebssteuerung.messaging.SelectrixGateway;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.selectrix.SelectrixMessage;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.CreationException;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

/**
 * Producer für Steuerung.
 *
 * @author dw
 */
@ApplicationScoped
public class SteuerungProducer {
  // Wichtig: Dependent, da nur so kein CDI-Proxy gebaut wird. Das wäre beim Marshalling zum Client (EJB, JAXB, Jason) im Wege.
  @Produces
  @Dependent
  private Steuerung steuerung;

  @Inject
  SelectrixGateway selectrixGateway;

  @PostConstruct
  private void init() {
    String xmlResourceName = System.getProperty("v5t11.anlage", "DemoAnlage") + ".xml";
    try {
      this.steuerung = XmlConverter.fromXml(Steuerung.class, xmlResourceName);
    } catch (Exception e) {
      throw new CreationException("Cannot read " + xmlResourceName, e);
    }

    List<Integer> adressen = this.steuerung.getAdressen();
    for (int i = 0; i <= 9; ++i) {
      adressen.add(i);
    }
    for (int i = 40; i <= 73; ++i) {
      adressen.add(i);
    }

    this.selectrixGateway.start(
        System.getProperty("v5t11.portName", "none"),
        Integer.parseInt(System.getProperty("v5t11.portSpeed", "9600")),
        System.getProperty("v5t11.ifTyp", "rautenhaus"),
        adressen);

    for (int adresse : adressen) {
      this.steuerung.onMessage(new SelectrixMessage(adresse, this.selectrixGateway.getValue(adresse)));
    }
  }

  @PreDestroy
  private void shutdown() {
    this.selectrixGateway.stop();
  }
}

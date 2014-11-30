package de.gedoplan.v5t11.betriebssteuerung.producer;

import de.gedoplan.baselibs.utils.xml.XmlConverter;
import de.gedoplan.v5t11.betriebssteuerung.messaging.SelectrixGateway;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.selectrix.SelectrixMessage;

import java.util.List;

import javax.annotation.PostConstruct;
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
public class SteuerungProducer
{
  // Wichtig: Dependent, da nur so kein CDI-Proxy gebaut wird. Das wäre beim Marchalling zum Client (EJB, JAXB, Jason) im Wege.
  @Produces
  @Dependent
  private Steuerung steuerung;

  @Inject
  SelectrixGateway  selectrixGateway;

  @PostConstruct
  private void init()
  {
    String xmlResourceName = System.getProperty("v5t11.anlage", "DemoAnlage") + ".xml";
    try
    {
      this.steuerung = XmlConverter.fromXml(Steuerung.class, xmlResourceName);
    }
    catch (Exception e)
    {
      throw new CreationException("Cannot read " + xmlResourceName, e);
    }

    List<Integer> adressen = this.steuerung.getAdressen();
    this.selectrixGateway.addAddressen(adressen);
    this.selectrixGateway.addAddressen(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);

    for (int adresse : adressen)
    {
      this.steuerung.onMessage(new SelectrixMessage(adresse, this.selectrixGateway.getValue(adresse)));
    }
  }
}

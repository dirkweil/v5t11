package de.gedoplan.v5t11.fahrstrassen;

import de.gedoplan.v5t11.fahrstrassen.service.ParcoursProducer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;

public class TestBase {

  protected Log log = LogFactory.getLog(getClass());

  @BeforeClass
  public static void initSysProps() {
    System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    System.setProperty(ParcoursProducer.CONFIG, "src/test/resources/test_parcours.xml");
  }

}

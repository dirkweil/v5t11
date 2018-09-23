package de.gedoplan.v5t11.fahrstrassen;

import de.gedoplan.v5t11.util.config.ConfigBase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;

public class TestBase {

  protected Log log = LogFactory.getLog(getClass());

  @BeforeClass
  public static void initSysProps() {
    System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
    if (System.getProperty(ConfigBase.PROPERTY_ANLAGE) == null) {
      System.setProperty(ConfigBase.PROPERTY_ANLAGE, "test");
    }
    System.setProperty("UNITTEST", "true");
  }

}

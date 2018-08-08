package de.gedoplan.v5t11.status;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;

public class TestBase {

  protected Log log = LogFactory.getLog(getClass());

  @BeforeClass
  public static void initLogging() {
    System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
  }

}

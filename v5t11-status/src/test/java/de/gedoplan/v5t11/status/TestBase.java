package de.gedoplan.v5t11.status;

import org.junit.BeforeClass;

public class TestBase {

  @BeforeClass
  public static void initLogging() {
    System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");
  }

}

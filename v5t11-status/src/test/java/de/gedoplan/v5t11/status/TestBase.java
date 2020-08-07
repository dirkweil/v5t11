package de.gedoplan.v5t11.status;

import de.gedoplan.v5t11.util.test.V5t11TestConfigDirExtension;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(V5t11TestConfigDirExtension.class)
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class TestBase {

  protected Log log = LogFactory.getLog(getClass());

  public static void delay(long millis) {
    try {
      Thread.sleep(millis);
    } catch (Exception e) {
    }
  }
}

package de.gedoplan.v5t11.strecken.gateway;

import de.gedoplan.v5t11.strecken.CdiTestBase;
import de.gedoplan.v5t11.strecken.entity.fahrweg.Gleisabschnitt;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.junit.Test;

public class GleisResourceClientTest extends CdiTestBase {

  @Inject
  GleisResourceClient gleisResourceClient;

  @Inject
  Log log;

  @Test
  public void test_01_getGleisabschnitt() throws Exception {
    Gleisabschnitt gleis = this.gleisResourceClient.getGleisabschnitt("test", "1");
    this.log.debug("Gleisabschnitt: " + gleis);
    this.log.debug("  isBesetzt: " + gleis.isBesetzt());
  }

  @Test
  public void test_02_getGleisabschnitte() throws Exception {
    for (Gleisabschnitt gleis : this.gleisResourceClient.getGleisabschnitte()) {
      this.log.debug("Gleisabschnitt: " + gleis);
      this.log.debug(" isBesetzt: " + gleis.isBesetzt());
    }
  }
}

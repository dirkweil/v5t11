package de.gedoplan.v5t11.strecken.gateway;

import de.gedoplan.v5t11.strecken.CdiTestBase;
import de.gedoplan.v5t11.strecken.entity.fahrweg.Weiche;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.junit.Test;

public class WeicheResourceClientTest extends CdiTestBase {

  @Inject
  WeicheResourceClient weicheResourceClient;

  @Inject
  Log log;

  @Test
  public void test_01_getWeiche() throws Exception {
    Weiche weiche = this.weicheResourceClient.getWeiche("test", "10");
    this.log.debug("Weiche: " + weiche);
    this.log.debug("  stellung: " + weiche.getStellung());
  }

  @Test
  public void test_02_getWeichen() throws Exception {
    for (Weiche weiche : this.weicheResourceClient.getWeichen()) {
      this.log.debug("Weiche: " + weiche);
      this.log.debug(" stellung: " + weiche.getStellung());
    }
  }
}

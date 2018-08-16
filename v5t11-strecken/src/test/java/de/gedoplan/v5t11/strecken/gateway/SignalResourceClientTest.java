package de.gedoplan.v5t11.strecken.gateway;

import de.gedoplan.v5t11.strecken.CdiTestBase;
import de.gedoplan.v5t11.strecken.entity.fahrweg.Signal;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.junit.Test;

public class SignalResourceClientTest extends CdiTestBase {

  @Inject
  SignalResourceClient signalResourceClient;

  @Inject
  Log log;

  @Test
  public void test_01_getSignal() throws Exception {
    Signal signal = this.signalResourceClient.getSignal("test", "P2");
    this.log.debug("Signal: " + signal);
    this.log.debug("  stellung: " + signal.getStellung());
    this.log.debug("  erlaubteStellungen: " + signal.getErlaubteStellungen());
    this.log.debug("  typ: " + signal.getTyp());
  }

  @Test
  public void test_02_getSignale() throws Exception {
    // System.out.println(this.signalResourceClient.getSignale());

    for (Signal signal : this.signalResourceClient.getSignale()) {
      this.log.debug("Signal: " + signal);
      this.log.debug(" stellung: " + signal.getStellung());
      this.log.debug(" erlaubteStellungen: " + signal.getErlaubteStellungen());
      this.log.debug(" typ: " + signal.getTyp());
    }
  }
}

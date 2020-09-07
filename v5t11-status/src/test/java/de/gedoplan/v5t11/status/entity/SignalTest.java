package de.gedoplan.v5t11.status.entity;

import de.gedoplan.v5t11.status.CdiTestBase;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class SignalTest extends CdiTestBase {

  @Inject
  Steuerung steuerung;

  @Test
  public void test_01_toShortJson() throws Exception {

    this.log.info("----- test_01_toShortJson -----");

    Signal signal = this.steuerung.getSignal("test", "P2");

    String json = JsonbWithIncludeVisibility.SHORT.toJson(signal);

    this.log.debug("JSON string: " + json);

    JSONAssert.assertEquals(""
        + "{\"bereich\":\"test\""
        + ",\"name\":\"P2\""
        + ",\"stellung\":\"" + signal.getStellung() + "\""
        + "}",
        json,
        true);
  }

  @Test
  public void test_02_toFullJson() throws Exception {

    this.log.info("----- test_02_toFullJson -----");

    Signal signal = this.steuerung.getSignal("test", "P2");

    String json = JsonbWithIncludeVisibility.FULL.toJson(signal);

    this.log.debug("JSON string: " + json);

    JSONAssert.assertEquals(""
        + "{\"bereich\":\"test\""
        + ",\"name\":\"P2\""
        + ",\"stellung\":\"" + signal.getStellung() + "\""
        + ",\"erlaubteStellungen\":[\"HALT\",\"FAHRT\",\"LANGSAMFAHRT\",\"RANGIERFAHRT\"]"
        + ",\"typ\":\"Hauptsperrsignal"
        + "\"}",
        json,
        true);
  }
}

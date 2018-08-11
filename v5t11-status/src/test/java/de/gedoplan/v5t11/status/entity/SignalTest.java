package de.gedoplan.v5t11.status.entity;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import de.gedoplan.v5t11.status.CdiTestBase;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.jsonb.JsonbWithIncludeVisibility;

import javax.inject.Inject;

import org.junit.Test;

public class SignalTest extends CdiTestBase {

  @Inject
  Steuerung steuerung;

  @Test
  public void test_01_toShortJson() throws Exception {

    Signal signal = this.steuerung.getSignal("test", "P2");

    String json = JsonbWithIncludeVisibility.SHORT.toJson(signal);

    this.log.debug("JSON string: " + json);

    assertThat(json, is(
        "{\"bereich\":\"test\""
            + ",\"name\":\"P2\""
            + ",\"stellung\":\"" + signal.getStellung() + "\""
            + "}"));
  }

  @Test
  public void test_02_toFullJson() throws Exception {

    Signal signal = this.steuerung.getSignal("test", "P2");

    String json = JsonbWithIncludeVisibility.FULL.toJson(signal);

    this.log.debug("JSON string: " + json);

    assertThat(json, is(
        "{\"bereich\":\"test\""
            + ",\"name\":\"P2\""
            + ",\"erlaubteStellungen\":[\"HALT\",\"FAHRT\",\"LANGSAMFAHRT\",\"RANGIERFAHRT\"]"
            + ",\"stellung\":\"" + signal.getStellung() + "\""
            + ",\"typ\":\"Hauptsperrsignal"
            + "\"}"));
  }
}

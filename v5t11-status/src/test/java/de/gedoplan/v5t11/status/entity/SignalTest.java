package de.gedoplan.v5t11.status.entity;

import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;
import de.gedoplan.v5t11.util.test.V5t11TestConfigDirExtension;

import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;

import io.quarkus.test.junit.QuarkusTestExtension;

@ExtendWith({ V5t11TestConfigDirExtension.class, QuarkusTestExtension.class })
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class SignalTest {

  @Inject
  Steuerung steuerung;

  @Inject
  Log log;

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

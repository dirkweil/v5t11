package de.gedoplan.v5t11.status.entity;

import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;
import de.gedoplan.v5t11.util.test.V5t11TestConfigDirExtension;

import jakarta.inject.Inject;
import jakarta.json.Json;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;

import io.quarkus.test.junit.QuarkusTestExtension;

@ExtendWith({ V5t11TestConfigDirExtension.class, QuarkusTestExtension.class })
@TestMethodOrder(MethodOrderer.MethodName.class)
public class SignalTest {

  @Inject
  Steuerung steuerung;

  @Inject
  Logger log;

  @Test
  public void test_01_toShortJson() throws Exception {

    this.log.info("----- test_01_toShortJson -----");

    Signal signal = this.steuerung.getSignal("test", "P2");

    String json = JsonbWithIncludeVisibility.SHORT.toJson(signal);

    this.log.debug("JSON string: " + json);

    String expected = Json.createObjectBuilder()
      .add("key", signal.getKey().toString())
      .add("lastChangeMillis", signal.getLastChangeMillis())
      .add("stellung", signal.getStellung().toString())
      .add("typ", signal.getTyp().toString())
      .build().toString();

    JSONAssert.assertEquals(expected, json, true);
  }

  @Test
  public void test_02_toFullJson() throws Exception {

    this.log.info("----- test_02_toFullJson -----");

    Signal signal = this.steuerung.getSignal("test", "P2");

    String json = JsonbWithIncludeVisibility.FULL.toJson(signal);

    this.log.debug("JSON string: " + json);

    String expected = Json.createObjectBuilder()
      .add("key", signal.getKey().toString())
      .add("lastChangeMillis", signal.getLastChangeMillis())
      .add("stellung", signal.getStellung().toString())
      .add("typ", signal.getTyp().toString())
      .build().toString();

    JSONAssert.assertEquals(expected, json, true);
  }
}

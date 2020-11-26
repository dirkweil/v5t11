package de.gedoplan.v5t11.status.entity;

import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;
import de.gedoplan.v5t11.util.test.V5t11TestConfigDirExtension;

import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.json.Json;

import org.jboss.logging.Logger;
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
  Logger log;

  @Test
  public void test_01_toShortJson() throws Exception {

    this.log.info("----- test_01_toShortJson -----");

    Signal signal = this.steuerung.getSignal("test", "P2");

    String json = JsonbWithIncludeVisibility.SHORT.toJson(signal);

    this.log.debug("JSON string: " + json);

    String expected = Json.createObjectBuilder()
        .add("key", signal.getKey().encode())
        .add("lastChangeMillis", signal.getLastChangeMillis())
        .add("stellung", signal.getStellung().getCode())
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
        .add("key", signal.getKey().encode())
        .add("lastChangeMillis", signal.getLastChangeMillis())
        .add("stellung", signal.getStellung().getCode())
        // TODO Jsonb scheint bei Arrays den TypeAdapter nicht zu benutzen (Bug in Yasson?)
        // .add("erlaubteStellungen", Json.createArrayBuilder(signal.getErlaubteStellungen().stream().map(s -> s.getCode()).collect(Collectors.toList())).build())
        .add("erlaubteStellungen", Json.createArrayBuilder(signal.getErlaubteStellungen().stream().map(s -> s.name()).collect(Collectors.toList())).build())
        .add("typ", signal.getTyp())
        .build().toString();

    JSONAssert.assertEquals(expected, json, true);
  }
}

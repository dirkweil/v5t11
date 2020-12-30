package de.gedoplan.v5t11.status.entity;

import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;
import de.gedoplan.v5t11.util.test.V5t11TestConfigDirExtension;

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
public class GleisabschnittTest {

  @Inject
  Steuerung steuerung;

  @Inject
  Logger log;

  @Test
  public void test_01_toJson() throws Exception {

    this.log.info("----- test_01_toJson -----");

    Gleisabschnitt gleisabschnitt = this.steuerung.getGleisabschnitt("test", "1");

    String json = JsonbWithIncludeVisibility.SHORT.toJson(gleisabschnitt);

    this.log.debug("JSON string: " + json);

    String expected = Json.createObjectBuilder()
        .add("key", gleisabschnitt.getKey().toString())
        .add("lastChangeMillis", gleisabschnitt.getLastChangeMillis())
        .add("besetzt", gleisabschnitt.isBesetzt())
        .build().toString();

    JSONAssert.assertEquals(expected, json, true);
  }
}

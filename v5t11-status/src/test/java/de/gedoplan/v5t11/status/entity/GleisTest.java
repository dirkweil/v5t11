package de.gedoplan.v5t11.status.entity;

import de.gedoplan.v5t11.status.entity.fahrweg.Gleis;
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
public class GleisTest {

  @Inject
  Steuerung steuerung;

  @Inject
  Logger log;

  @Test
  public void test_01_toJson() throws Exception {

    this.log.info("----- test_01_toJson -----");

    Gleis gleis = this.steuerung.getGleis("test", "1");

    String json = JsonbWithIncludeVisibility.SHORT.toJson(gleis);

    this.log.debug("JSON string: " + json);

    String expected = Json.createObjectBuilder()
        .add("key", gleis.getKey().toString())
        .add("lastChangeMillis", gleis.getLastChangeMillis())
        .add("besetzt", gleis.isBesetzt())
        .build().toString();

    JSONAssert.assertEquals(expected, json, true);
  }
}

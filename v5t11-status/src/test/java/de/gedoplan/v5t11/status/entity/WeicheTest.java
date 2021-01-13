package de.gedoplan.v5t11.status.entity;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
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
public class WeicheTest {

  @Inject
  Steuerung steuerung;

  @Inject
  Logger log;

  @Test
  public void test_01_toShortJson() throws Exception {

    this.log.info("----- test_01_toShortJson -----");

    Weiche weiche = this.steuerung.getWeiche("test", "10");

    String json = JsonbWithIncludeVisibility.SHORT.toJson(weiche);

    this.log.debug("JSON string: " + json);

    String expected = Json.createObjectBuilder()
        .add("key", weiche.getKey().toString())
        .add("lastChangeMillis", weiche.getLastChangeMillis())
        .add("stellung", weiche.getStellung().toString())
        .build().toString();

    JSONAssert.assertEquals(expected, json, true);
  }

  @Test
  public void test_02_toFullJson() throws Exception {

    this.log.info("----- test_02_toFullJson -----");

    Weiche weiche = this.steuerung.getWeiche("test", "10");

    String json = JsonbWithIncludeVisibility.FULL.toJson(weiche);

    this.log.debug("JSON string: " + json);

    String expected = Json.createObjectBuilder()
        .add("key", weiche.getKey().toString())
        .add("lastChangeMillis", weiche.getLastChangeMillis())
        .add("stellung", weiche.getStellung().toString())
        .add("gleisName", weiche.getGleisName())
        .build().toString();

    JSONAssert.assertEquals(expected, json, true);
  }

  @Test
  public void test_03_fromShortJson() throws Exception {

    // this.log.info("----- test_03_fromShortJson -----");

    String json = "{\"key\":\"10@test\",\"lastChangeMillis\":12345,\"stellung\":\"A\"}";

    Weiche weiche = JsonbWithIncludeVisibility.SHORT.fromJson(json, Weiche.class);

    assertThat("Bereich", weiche.getBereich(), is("test"));
    assertThat("Name", weiche.getName(), is("10"));
    assertThat("Lastchange", weiche.getLastChangeMillis(), is(12345L));

    /*
     * Achtung: Mehr kann nicht gestestet werden: Stellung ist bei fromJson nicht included!
     */
    // assertThat("Stellung", weiche.getStellung(), is(WeichenStellung.ABZWEIGEND));

  }
}

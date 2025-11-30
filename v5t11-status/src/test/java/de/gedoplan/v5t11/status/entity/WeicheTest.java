package de.gedoplan.v5t11.status.entity;

import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.testenvironment.profile.V5T11Test;
import de.gedoplan.v5t11.util.jsonb.JsonbWithVisibility;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.bind.JsonbBuilder;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@QuarkusTest
@TestProfile(V5T11Test.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class WeicheTest {

  @Inject
  Steuerung steuerung;

  @Inject
  Logger log;

  @Test
  public void test_02_toFullJson() throws Exception {

    this.log.info("----- test_02_toFullJson -----");

    Weiche weiche = this.steuerung.getWeiche("test", "10");

    String json = JsonbBuilder.create().toJson(weiche);

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

    Weiche weiche = JsonbWithVisibility.SHORT.fromJson(json, Weiche.class);

    assertThat("Bereich", weiche.getBereich(), is("test"));
    assertThat("Name", weiche.getName(), is("10"));
    assertThat("Lastchange", weiche.getLastChangeMillis(), is(12345L));

    /*
     * Achtung: Mehr kann nicht gestestet werden: Stellung ist bei fromJson nicht included!
     */
    // assertThat("Stellung", weiche.getStellung(), is(WeichenStellung.ABZWEIGEND));

  }
}

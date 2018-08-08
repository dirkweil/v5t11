package de.gedoplan.v5t11.status.entity;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import de.gedoplan.v5t11.status.CdiTestBase;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.junit.Test;

public class WeicheTest extends CdiTestBase {

  @Inject
  Steuerung steuerung;

  @Test
  public void test_01_toJson() throws Exception {

    Weiche weiche = this.steuerung.getWeiche("test", "10");

    Jsonb jsonb = JsonbBuilder.create();

    String json = jsonb.toJson(weiche);

    this.log.debug("JSON string: " + json);

    assertThat(json, is("{\"bereich\":\"test\",\"name\":\"10\",\"stellung\":\"GERADE\"}"));
  }
}

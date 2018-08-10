package de.gedoplan.v5t11.status.entity;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import de.gedoplan.v5t11.status.CdiTestBase;
import de.gedoplan.v5t11.status.entity.lok.Lok;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.junit.Test;

public class LokTest extends CdiTestBase {

  @Inject
  Steuerung steuerung;

  @Test
  public void test_01_toJson() throws Exception {

    final String lokId = "212 216-6";

    Lok lok = this.steuerung.getLok(lokId);

    Jsonb jsonb = JsonbBuilder.create();

    String json = jsonb.toJson(lok);

    this.log.debug("JSON string: " + json);

    assertThat(json, is(
        "{\"geschwindigkeit\":" + lok.getGeschwindigkeit()
            + ",\"id\":\"" + lokId + "\""
            + ",\"licht\":" + lok.isLicht()
            + ",\"rueckwaerts\":" + lok.isRueckwaerts()
            + "}"));
  }
}

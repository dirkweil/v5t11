package de.gedoplan.v5t11.status.entity;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import de.gedoplan.v5t11.status.CdiTestBase;
import de.gedoplan.v5t11.status.entity.lok.Lok;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import javax.inject.Inject;

import org.junit.Test;

public class LokTest extends CdiTestBase {

  @Inject
  Steuerung steuerung;

  @Test
  public void test_01_toShortJson() throws Exception {

    final String lokId = "212 216-6";

    Lok lok = this.steuerung.getLok(lokId);

    String json = JsonbWithIncludeVisibility.SHORT.toJson(lok);

    this.log.debug("JSON string: " + json);

    assertThat(json, is(
        "{\"geschwindigkeit\":" + lok.getGeschwindigkeit()
            + ",\"id\":\"" + lokId + "\""
            + ",\"licht\":" + lok.isLicht()
            + ",\"rueckwaerts\":" + lok.isRueckwaerts()
            + "}"));
  }

  @Test
  public void test_02_toFullJson() throws Exception {

    final String lokId = "212 216-6";

    Lok lok = this.steuerung.getLok(lokId);

    String json = JsonbWithIncludeVisibility.FULL.toJson(lok);

    this.log.debug("JSON string: " + json);

    assertThat(json, is(
        "{\"geschwindigkeit\":" + lok.getGeschwindigkeit()
            + ",\"id\":\"" + lokId + "\""
            + ",\"licht\":" + lok.isLicht()
            + ",\"lokdecoderAdressen\":[5]"
            + ",\"lokdecoderTyp\":\"Tr66825\""
            + ",\"rueckwaerts\":" + lok.isRueckwaerts()
            + "}"));
  }
}

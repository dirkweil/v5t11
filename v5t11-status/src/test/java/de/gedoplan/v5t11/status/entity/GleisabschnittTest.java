package de.gedoplan.v5t11.status.entity;

import de.gedoplan.v5t11.status.CdiTestBase;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import javax.inject.Inject;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class GleisabschnittTest extends CdiTestBase {

  @Inject
  Steuerung steuerung;

  @Test
  public void test_01_toJson() throws Exception {

    this.log.info("----- test_01_toJson -----");

    Gleisabschnitt gleisabschnitt = this.steuerung.getGleisabschnitt("test", "1");

    String json = JsonbWithIncludeVisibility.SHORT.toJson(gleisabschnitt);

    this.log.debug("JSON string: " + json);

    JSONAssert.assertEquals(""
        + "{\"bereich\":\"test\""
        + ",\"name\":\"1\""
        + ",\"besetzt\":" + gleisabschnitt.isBesetzt()
        + "}",
        json,
        true);
  }
}

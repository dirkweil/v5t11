package de.gedoplan.v5t11.status.entity;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import de.gedoplan.v5t11.status.CdiTestBase;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.status.jsonb.JsonbWithIncludeVisibility;

import javax.inject.Inject;

import org.junit.Test;

public class GleisabschnittTest extends CdiTestBase {

  @Inject
  Steuerung steuerung;

  @Test
  public void test_01_toJson() throws Exception {

    Gleisabschnitt gleisabschnitt = this.steuerung.getGleisabschnitt("test", "1");

    String json = JsonbWithIncludeVisibility.SHORT.toJson(gleisabschnitt);

    this.log.debug("JSON string: " + json);

    assertThat(json, is(
        "{\"bereich\":\"test\""
            + ",\"name\":\"1\""
            + ",\"besetzt\":" + gleisabschnitt.isBesetzt()
            + "}"));
  }
}

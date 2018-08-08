package de.gedoplan.v5t11.status.entity;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import de.gedoplan.v5t11.status.CdiTestBase;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;

import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import org.junit.Test;

public class GleisabschnittTest extends CdiTestBase {

  @Inject
  Steuerung steuerung;

  @Test
  public void test_01_toJson() throws Exception {

    Gleisabschnitt gleisabschnitt = this.steuerung.getGleisabschnitt("test", "1");

    Jsonb jsonb = JsonbBuilder.create();

    String json = jsonb.toJson(gleisabschnitt);

    this.log.debug("JSON string: " + json);

    assertThat(json, is("{\"bereich\":\"test\",\"name\":\"1\",\"besetzt\":false}"));
  }
}

package de.gedoplan.v5t11.status.entity;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import de.gedoplan.v5t11.status.CdiTestBase;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.jsonb.JsonbWithIncludeVisibility;

import javax.inject.Inject;

import org.junit.Test;

public class WeicheTest extends CdiTestBase {

  @Inject
  Steuerung steuerung;

  @Test
  public void test_01_toJson() throws Exception {

    Weiche weiche = this.steuerung.getWeiche("test", "10");

    String json = JsonbWithIncludeVisibility.SHORT.toJson(weiche);

    this.log.debug("JSON string: " + json);

    assertThat(json, is("{\"bereich\":\"test\",\"name\":\"10\",\"stellung\":\"" + weiche.getStellung() + "\"}"));
  }
}

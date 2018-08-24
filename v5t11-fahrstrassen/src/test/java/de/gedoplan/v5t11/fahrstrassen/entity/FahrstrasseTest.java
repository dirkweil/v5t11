package de.gedoplan.v5t11.fahrstrassen.entity;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import de.gedoplan.v5t11.fahrstrassen.TestBase;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import javax.inject.Inject;

import org.apache.meecrowave.junit.MonoMeecrowave;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

@RunWith(MonoMeecrowave.Runner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FahrstrasseTest extends TestBase {

  @Inject
  Parcours parcours;

  @Test
  public void test_01_toShortJson() throws Exception {

    Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse("show", "11-1-S");

    String json = JsonbWithIncludeVisibility.SHORT.toJson(fahrstrasse);

    this.log.debug("JSON string: " + json);

    assertThat(json, is("{"
        + "\"bereich\":\"show\""
        + ",\"name\":\"11-1-S\""
        + "}"));
  }

}

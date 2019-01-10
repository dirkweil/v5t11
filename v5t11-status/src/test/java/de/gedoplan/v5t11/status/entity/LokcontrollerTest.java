package de.gedoplan.v5t11.status.entity;

import de.gedoplan.v5t11.status.CdiTestBase;
import de.gedoplan.v5t11.status.entity.baustein.Lokcontroller;
import de.gedoplan.v5t11.status.entity.lok.Lok;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import javax.inject.Inject;

import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class LokcontrollerTest extends CdiTestBase {

  @Inject
  Steuerung steuerung;

  @Test
  public void test_01_toShortJson() throws Exception {

    final String id = "LokControl 1";

    Lokcontroller lokcontroller = this.steuerung.getLokcontroller(id);

    String json = JsonbWithIncludeVisibility.SHORT.toJson(lokcontroller);

    this.log.debug("JSON string: " + json);

    Lok lok = lokcontroller.getLok();
    String lokId = lok != null ? lok.getId() : null;

    JSONAssert.assertEquals(""
        + "{\"id\":\"" + id + "\""
        + ",\"lokId\":" + lokId
        + "}",
        json,
        true);
  }

}

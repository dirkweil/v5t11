package de.gedoplan.v5t11.status.entity;

import static org.junit.Assert.assertTrue;

import de.gedoplan.v5t11.status.CdiTestBase;
import de.gedoplan.v5t11.status.StatusEventCollector;
import de.gedoplan.v5t11.status.entity.lok.Lok;
import de.gedoplan.v5t11.status.entity.lok.Lok.FunktionConfig;
import de.gedoplan.v5t11.status.testenvironment.service.TestLokRepository;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class LokTest extends CdiTestBase {

  @Inject
  Steuerung steuerung;

  @Inject
  StatusEventCollector statusEventCollector;

  @Test
  public void test_01_toShortJson() throws Exception {

    final String lokId = TestLokRepository.testLok103.getId();

    Lok lok = this.steuerung.getLok(lokId);

    // System.out.println("##### " + lok);
    // Method method = lok.getClass().getMethod("getId", null);
    // System.out.println(" ##### annotations: " + Stream.of(method.getAnnotations()).map(a -> a.toString()).collect(Collectors.joining(",")));

    String json = JsonbWithIncludeVisibility.SHORT.toJson(lok);

    this.log.debug("JSON string: " + json);

    JSONAssert.assertEquals(""
        + "{\"aktiv\":" + lok.isAktiv()
        + ",\"fahrstufe\":" + lok.getFahrstufe()
        + ",\"funktionStatus\":" + lok.getFunktionStatus()
        + ",\"id\":\"" + lokId + "\""
        + ",\"licht\":" + lok.isLicht()
        + ",\"rueckwaerts\":" + lok.isRueckwaerts()
        + "}",
        json,
        true);
  }

  @Test
  public void test_02_toFullJson() throws Exception {

    final String lokId = TestLokRepository.testLok103.getId();

    Lok lok = this.steuerung.getLok(lokId);

    String json = JsonbWithIncludeVisibility.FULL.toJson(lok);

    this.log.debug("JSON string: " + json);

    JSONAssert.assertEquals(""
        + "{\"aktiv\":" + lok.isAktiv()
        + ",\"fahrstufe\":" + lok.getFahrstufe()
        + ",\"funktionStatus\":" + lok.getFunktionStatus()
        + ",\"id\":\"" + lokId + "\""
        + ",\"licht\":" + lok.isLicht()
        + ",\"maxFahrstufe\":" + lok.getMaxFahrstufe()
        + ",\"rueckwaerts\":" + lok.isRueckwaerts()
        // + ",\"funktionConfigs\":{\"2\":{\"beschreibung\":\"Motorgeräusch\",\"impuls\":false},\"7\":{\"beschreibung\":\"Pfiff\",\"impuls\":true}}"
        + ",\"funktionConfigs\":" + toJson(lok.getFunktionConfigs())
        + "}",
        json,
        true);
  }

  private String toJson(Map<@Min(1) @Max(16) Integer, @NotNull FunktionConfig> funktionConfigs) {

    return funktionConfigs.entrySet().stream()
        .map(entry -> "\"" + entry.getKey() + "\":{\"beschreibung\":\"" + entry.getValue().getBeschreibung() + "\",\"impuls\":" + entry.getValue().isImpuls() + "}")
        .collect(Collectors.joining(",", "{", "}"));

  }

  @Test
  public void test_03_events() throws Exception {

    for (Lok lok : this.steuerung.getLoks()) {
      lok.reset();

      this.statusEventCollector.clear();
      lok.setAktiv(true);
      assertTrue("Statuswechselmeldung fuer " + lok + " erfolgt", this.statusEventCollector.getEvents().contains(lok));

      this.statusEventCollector.clear();
      lok.setLicht(true);
      assertTrue("Statuswechselmeldung fuer " + lok + " erfolgt", this.statusEventCollector.getEvents().contains(lok));

      this.statusEventCollector.clear();
      lok.setFahrstufe(10);
      assertTrue("Statuswechselmeldung fuer " + lok + " erfolgt", this.statusEventCollector.getEvents().contains(lok));

      this.statusEventCollector.clear();
      if (!lok.getFunktionConfigs().isEmpty()) {
        int fn = lok.getFunktionConfigs().keySet().iterator().next();
        lok.setFunktion(fn, true);
        assertTrue("Statuswechselmeldung fuer " + lok + " erfolgt", this.statusEventCollector.getEvents().contains(lok));
      }
    }
  }

  @Test
  public void test_04_something() throws Exception {

    this.steuerung.getZentrale().setGleisspannung(true);

    final String lokId = TestLokRepository.testLok103.getId();

    Lok lok = this.steuerung.getLok(lokId);

    lok.setAktiv(true);

    for (int i = 0; i < 10; ++i) {
      lok.setLicht((i % 2) != 0);

      lok.setFunktion(2, (i % 2) != 0);

      delay(1000);
    }

    this.steuerung.getZentrale().setGleisspannung(false);
  }
}

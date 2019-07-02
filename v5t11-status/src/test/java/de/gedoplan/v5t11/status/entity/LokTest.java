package de.gedoplan.v5t11.status.entity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import de.gedoplan.v5t11.status.CdiTestBase;
import de.gedoplan.v5t11.status.StatusEventCollector;
import de.gedoplan.v5t11.status.entity.baustein.zentrale.DummyZentrale;
import de.gedoplan.v5t11.status.entity.lok.Lok;
import de.gedoplan.v5t11.status.entity.lok.Lok.FunktionConfig;
import de.gedoplan.v5t11.status.testenvironment.service.TestLokRepository;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import java.util.Map;
import java.util.concurrent.Semaphore;
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

    final String lokId = TestLokRepository.lok103_003_0.getId();

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

    final String lokId = TestLokRepository.lok103_003_0.getId();

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
        + ",\"funktionConfigs\":" + toJson(lok.getFunktionConfigs())
        + "}",
        json,
        true);
  }

  private String toJson(Map<@Min(1) @Max(16) Integer, @NotNull FunktionConfig> funktionConfigs) {

    return funktionConfigs.entrySet().stream()
        .map(entry -> "\"" + entry.getKey() + "\":{\"beschreibung\":\"" + entry.getValue().getBeschreibung() + "\",\"impuls\":" + entry.getValue().isImpuls() + ",\"horn\":"
            + entry.getValue().isHorn() + "}")
        .collect(Collectors.joining(",", "{", "}"));

  }

  @Test
  public void test_03_events() throws Exception {

    this.steuerung.getZentrale().setGleisspannung(true);

    new Semaphore(0).acquire();
  }

  @Test
  public void test_04_sx1_events() throws Exception {

    this.steuerung.getZentrale().setGleisspannung(true);

    String lokId = TestLokRepository.lok151_032_0.getId();
    Lok lok = this.steuerung.getLok(lokId);
    assertNotNull("Lok " + lokId + " in Testdaten", lok);
    assertEquals("Lok-Typ von " + lokId, SystemTyp.SX1, lok.getSystemTyp());

    lok.reset();

    this.statusEventCollector.clear();
    lok.setAktiv(true);
    assertTrue("Statuswechselmeldung fuer " + lok + " erfolgt und kein weiterer Event", this.statusEventCollector.getEvents().contains(lok) && this.statusEventCollector.getEvents().size() == 1);

    throttle();

    this.statusEventCollector.clear();
    lok.setLicht(true);
    assertTrue("Statuswechselmeldung fuer " + lok + " erfolgt und kein weiterer Event", this.statusEventCollector.getEvents().contains(lok) && this.statusEventCollector.getEvents().size() == 1);

    throttle();

    this.statusEventCollector.clear();
    lok.setFahrstufe(10);
    assertTrue("Statuswechselmeldung fuer " + lok + " erfolgt und kein weiterer Event", this.statusEventCollector.getEvents().contains(lok) && this.statusEventCollector.getEvents().size() == 1);

    throttle();

    this.statusEventCollector.clear();
    if (!lok.getFunktionConfigs().isEmpty()) {
      int fn = lok.getFunktionConfigs().keySet().iterator().next();
      lok.setFunktion(fn, true);
      assertTrue("Statuswechselmeldung fuer " + lok + " erfolgt und kein weiterer Event", this.statusEventCollector.getEvents().contains(lok) && this.statusEventCollector.getEvents().size() == 1);
    }

    lok.reset();
    lok.setAktiv(false);
    this.steuerung.getZentrale().setGleisspannung(false);

    this.steuerung.awaitSync();
  }

  @Test
  public void test_05_sx2_events() throws Exception {

    this.steuerung.getZentrale().setGleisspannung(true);

    String lokId = TestLokRepository.lok217_001_7.getId();
    Lok lok = this.steuerung.getLok(lokId);
    assertNotNull("Lok " + lokId + " in Testdaten", lok);
    assertEquals("Lok-Typ von " + lokId, SystemTyp.SX2, lok.getSystemTyp());

    lok.reset();

    this.statusEventCollector.clear();
    lok.setAktiv(true);
    assertTrue("Statuswechselmeldung fuer " + lok + " erfolgt und kein weiterer Event", this.statusEventCollector.getEvents().contains(lok) && this.statusEventCollector.getEvents().size() == 1);

    throttle();

    this.statusEventCollector.clear();
    lok.setLicht(true);
    assertTrue("Statuswechselmeldung fuer " + lok + " erfolgt und kein weiterer Event", this.statusEventCollector.getEvents().contains(lok) && this.statusEventCollector.getEvents().size() == 1);

    throttle();

    this.statusEventCollector.clear();
    lok.setFahrstufe(10);
    assertTrue("Statuswechselmeldung fuer " + lok + " erfolgt und kein weiterer Event", this.statusEventCollector.getEvents().contains(lok) && this.statusEventCollector.getEvents().size() == 1);

    throttle();

    this.statusEventCollector.clear();
    if (!lok.getFunktionConfigs().isEmpty()) {
      int fn = lok.getFunktionConfigs().keySet().iterator().next();
      lok.setFunktion(fn, true);
      assertTrue("Statuswechselmeldung fuer " + lok + " erfolgt und kein weiterer Event", this.statusEventCollector.getEvents().contains(lok) && this.statusEventCollector.getEvents().size() == 1);
    }

    throttle();

    lok.reset();
    lok.setAktiv(false);
    this.steuerung.getZentrale().setGleisspannung(false);

    this.steuerung.awaitSync();
  }

  @Test
  public void test_06_dcc_events() throws Exception {

    this.steuerung.getZentrale().setGleisspannung(true);

    String lokId = TestLokRepository.lok103_003_0.getId();
    Lok lok = this.steuerung.getLok(lokId);
    assertNotNull("Lok " + lokId + " in Testdaten", lok);
    assertEquals("Lok-Typ von " + lokId, SystemTyp.DCC, lok.getSystemTyp());

    lok.reset();

    this.statusEventCollector.clear();
    lok.setAktiv(true);
    assertTrue("Statuswechselmeldung fuer " + lok + " erfolgt und kein weiterer Event", this.statusEventCollector.getEvents().contains(lok) && this.statusEventCollector.getEvents().size() == 1);

    throttle();

    this.statusEventCollector.clear();
    lok.setLicht(true);
    assertTrue("Statuswechselmeldung fuer " + lok + " erfolgt und kein weiterer Event", this.statusEventCollector.getEvents().contains(lok) && this.statusEventCollector.getEvents().size() == 1);

    throttle();

    this.statusEventCollector.clear();
    lok.setFahrstufe(10);
    assertTrue("Statuswechselmeldung fuer " + lok + " erfolgt und kein weiterer Event", this.statusEventCollector.getEvents().contains(lok) && this.statusEventCollector.getEvents().size() == 1);

    throttle();

    this.statusEventCollector.clear();
    if (!lok.getFunktionConfigs().isEmpty()) {
      int fn = lok.getFunktionConfigs().keySet().iterator().next();
      lok.setFunktion(fn, true);
      assertTrue("Statuswechselmeldung fuer " + lok + " erfolgt und kein weiterer Event", this.statusEventCollector.getEvents().contains(lok) && this.statusEventCollector.getEvents().size() == 1);
    }

    throttle();

    lok.reset();
    lok.setAktiv(false);
    this.steuerung.getZentrale().setGleisspannung(false);

    this.steuerung.awaitSync();
  }

  private void throttle() {
    if (this.steuerung.getZentrale() instanceof DummyZentrale) {
      return;
    }

    delay(1000);
  }
}

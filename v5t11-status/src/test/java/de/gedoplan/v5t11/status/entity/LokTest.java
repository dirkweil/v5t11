package de.gedoplan.v5t11.status.entity;

import static org.junit.jupiter.api.Assertions.*;

import de.gedoplan.v5t11.status.CdiTestBase;
import de.gedoplan.v5t11.status.StatusEventCollector;
import de.gedoplan.v5t11.status.entity.baustein.zentrale.DummyZentrale;
import de.gedoplan.v5t11.status.entity.lok.Lok;
import de.gedoplan.v5t11.status.entity.lok.Lok.LokFunktion;
import de.gedoplan.v5t11.status.service.init.TestLokData;
import de.gedoplan.v5t11.util.domain.entity.SystemTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

public class LokTest extends CdiTestBase {

  @Inject
  Steuerung steuerung;

  @Inject
  StatusEventCollector statusEventCollector;

  @Test
  public void test_01_toShortJson() throws Exception {

    String lokId = TestLokData.lok103_003_0.getId();

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

    String lokId = TestLokData.lok103_003_0.getId();

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
        + ",\"funktionen\":" + toJson(lok.getFunktionen())
        + "}",
        json,
        true);
  }

  private String toJson(Set<@NotNull LokFunktion> set) {

    return set
        .stream()
        .map(entry -> "{"
            + "\"beschreibung\":\"" + entry.getBeschreibung() + "\","
            + "\"gruppe\":\"" + entry.getGruppe() + "\","
            + "\"horn\":" + entry.isHorn() + ","
            + "\"impuls\":" + entry.isImpuls() + ","
            + "\"mask\":" + entry.getMask() + ","
            + "\"nr\":" + entry.getNr() + ","
            + "\"value\":" + entry.getValue()
            + "}")
        .collect(Collectors.joining(",", "[", "]"));

  }

  // @Test
  // public void test_03_events() throws Exception {
  //
  // this.steuerung.getZentrale().setGleisspannung(true);
  //
  // new Semaphore(0).acquire();
  // }

  @Test
  public void test_04_sx1_events() throws Exception {

    this.steuerung.getZentrale().setGleisspannung(true);

    String lokId = TestLokData.lok210_004_8.getId();
    Lok lok = this.steuerung.getLok(lokId);
    assertNotNull(lok, "Lok " + lokId + " in Testdaten");
    assertEquals(lok.getSystemTyp(), SystemTyp.SX1, "Lok-Typ von " + lokId);

    lok.reset();

    this.statusEventCollector.clear();
    lok.setAktiv(true);
    assertSingleStatusEvent(lok);

    throttle();

    this.statusEventCollector.clear();
    lok.setLicht(true);
    assertSingleStatusEvent(lok);

    throttle();

    this.statusEventCollector.clear();
    lok.setFahrstufe(10);
    assertSingleStatusEvent(lok);

    throttle();

    this.statusEventCollector.clear();
    if (!lok.getFunktionen().isEmpty()) {
      LokFunktion fn = lok.getFunktionen().iterator().next();
      fn.setAktiv(true);
      assertSingleStatusEvent(lok);
    }

    lok.reset();
    lok.setAktiv(false);
    this.steuerung.getZentrale().setGleisspannung(false);

    this.steuerung.awaitSync();
  }

  @Test
  public void test_05_sx2_events() throws Exception {

    this.steuerung.getZentrale().setGleisspannung(true);

    String lokId = TestLokData.lok217_001_7.getId();
    Lok lok = this.steuerung.getLok(lokId);
    assertNotNull(lok, "Lok " + lokId + " in Testdaten");
    assertEquals(SystemTyp.SX2, lok.getSystemTyp(), "Lok-Typ von " + lokId);

    lok.reset();

    this.statusEventCollector.clear();
    lok.setAktiv(true);
    assertSingleStatusEvent(lok);

    throttle();

    this.statusEventCollector.clear();
    lok.setLicht(true);
    assertSingleStatusEvent(lok);

    throttle();

    this.statusEventCollector.clear();
    lok.setFahrstufe(10);
    assertSingleStatusEvent(lok);

    throttle();

    this.statusEventCollector.clear();
    if (!lok.getFunktionen().isEmpty()) {
      LokFunktion fn = lok.getFunktionen().iterator().next();
      fn.setAktiv(true);
      assertSingleStatusEvent(lok);
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

    String lokId = TestLokData.lok103_003_0.getId();
    Lok lok = this.steuerung.getLok(lokId);
    assertNotNull(lok, "Lok " + lokId + " in Testdaten");
    assertEquals(SystemTyp.DCC, lok.getSystemTyp(), "Lok-Typ von " + lokId);

    lok.reset();

    this.statusEventCollector.clear();
    lok.setAktiv(true);
    assertSingleStatusEvent(lok);

    throttle();

    this.statusEventCollector.clear();
    lok.setLicht(true);
    assertSingleStatusEvent(lok);

    throttle();

    this.statusEventCollector.clear();
    lok.setFahrstufe(10);
    assertSingleStatusEvent(lok);

    throttle();

    this.statusEventCollector.clear();
    if (!lok.getFunktionen().isEmpty()) {
      LokFunktion fn = lok.getFunktionen().iterator().next();
      fn.setAktiv(true);
      assertSingleStatusEvent(lok);
    }

    throttle();

    lok.reset();
    lok.setAktiv(false);
    this.steuerung.getZentrale().setGleisspannung(false);

    this.steuerung.awaitSync();
  }

  private void assertSingleStatusEvent(Lok lok) {
    assertTrue(this.statusEventCollector.getEvents().contains(lok) && this.statusEventCollector.getEvents().size() == 1, "Statuswechselmeldung fuer " + lok + " erfolgt und kein weiterer Event");
  }

  private void throttle() {
    if (this.steuerung.getZentrale() instanceof DummyZentrale) {
      return;
    }

    delay(1000);
  }
}

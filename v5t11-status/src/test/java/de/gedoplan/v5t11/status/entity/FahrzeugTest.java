package de.gedoplan.v5t11.status.entity;

import static org.junit.jupiter.api.Assertions.*;

import de.gedoplan.v5t11.status.StatusEventCollector;
import de.gedoplan.v5t11.status.entity.baustein.zentrale.DummyZentrale;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.status.testenvironment.service.TestFahrzeugRepository;
import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;
import de.gedoplan.v5t11.util.test.V5t11TestConfigDirExtension;

import javax.inject.Inject;
import javax.json.Json;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;

import io.quarkus.test.junit.QuarkusTestExtension;

@ExtendWith({ V5t11TestConfigDirExtension.class, QuarkusTestExtension.class })
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class FahrzeugTest {

  @Inject
  Steuerung steuerung;

  @Inject
  StatusEventCollector statusEventCollector;

  @Inject
  Logger log;

  @Test
  public void test_01_toShortJson() throws Exception {

    FahrzeugId fahrzeugId = TestFahrzeugRepository.lok103_003_0.getId();
    Fahrzeug fahrzeug = this.steuerung.getFahrzeug(fahrzeugId);

    String json = JsonbWithIncludeVisibility.SHORT.toJson(fahrzeug);

    this.log.debug("JSON string: " + json);

    String expected = Json.createObjectBuilder()
        .add("id", fahrzeug.getId().encode())
        .add("lastChangeMillis", fahrzeug.getLastChangeMillis())
        .add("aktiv", fahrzeug.isAktiv())
        .add("fahrstufe", fahrzeug.getFahrstufe())
        .add("licht", fahrzeug.isLicht())
        .add("rueckwaerts", fahrzeug.isRueckwaerts())
        .add("fktBits", fahrzeug.getFktBits())
        .build().toString();

    JSONAssert.assertEquals(expected, json, true);
  }

  @Test
  public void test_04_sx1_events() throws Exception {

    this.steuerung.getZentrale().setGleisspannung(true);

    FahrzeugId lokId = TestFahrzeugRepository.lok210_004_8.getId();
    Fahrzeug lok = this.steuerung.getFahrzeug(lokId);
    assertNotNull(lok, "Lok " + lokId + " in Testdaten");
    assertEquals(lok.getId().getSystemTyp(), SystemTyp.SX1, "Lok-Typ von " + lokId);

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

    lok.reset();
    lok.setAktiv(false);
    this.steuerung.getZentrale().setGleisspannung(false);

    this.steuerung.awaitSync();
  }

  @Test
  public void test_05_sx2_events() throws Exception {

    this.steuerung.getZentrale().setGleisspannung(true);

    FahrzeugId lokId = TestFahrzeugRepository.lok217_001_7.getId();
    Fahrzeug lok = this.steuerung.getFahrzeug(lokId);
    assertNotNull(lok, "Lok " + lokId + " in Testdaten");
    assertEquals(SystemTyp.SX2, lok.getId().getSystemTyp(), "Lok-Typ von " + lokId);

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

    lok.reset();
    lok.setAktiv(false);
    this.steuerung.getZentrale().setGleisspannung(false);

    this.steuerung.awaitSync();
  }

  @Test
  public void test_06_dcc_events() throws Exception {

    this.steuerung.getZentrale().setGleisspannung(true);

    FahrzeugId lokId = TestFahrzeugRepository.lok103_003_0.getId();
    Fahrzeug lok = this.steuerung.getFahrzeug(lokId);
    assertNotNull(lok, "Lok " + lokId + " in Testdaten");
    assertEquals(SystemTyp.DCC, lok.getId().getSystemTyp(), "Lok-Typ von " + lokId);

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

    lok.reset();
    lok.setAktiv(false);
    this.steuerung.getZentrale().setGleisspannung(false);

    this.steuerung.awaitSync();
  }

  private void assertSingleStatusEvent(Fahrzeug lok) {
    assertTrue(this.statusEventCollector.getEvents().contains(lok) && this.statusEventCollector.getEvents().size() == 1, "Statuswechselmeldung fuer " + lok + " erfolgt und kein weiterer Event");
  }

  private void throttle() {
    if (this.steuerung.getZentrale() instanceof DummyZentrale) {
      return;
    }

    try {
      Thread.sleep(1000);
    } catch (Exception e) {
    }
  }
}

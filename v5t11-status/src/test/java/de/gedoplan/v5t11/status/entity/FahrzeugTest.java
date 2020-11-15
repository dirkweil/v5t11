package de.gedoplan.v5t11.status.entity;

import static org.junit.jupiter.api.Assertions.*;

import de.gedoplan.v5t11.status.StatusEventCollector;
import de.gedoplan.v5t11.status.entity.baustein.zentrale.DummyZentrale;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.status.entity.fahrzeug.FahrzeugId;
import de.gedoplan.v5t11.status.testenvironment.service.TestFahrzeugRepository;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;
import de.gedoplan.v5t11.util.test.V5t11TestConfigDirExtension;

import javax.inject.Inject;

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

    FahrzeugId lokId = TestFahrzeugRepository.lok103_003_0.getId();
    Fahrzeug lok = this.steuerung.getFahrzeug(lokId);

    // System.out.println("##### " + lok);
    // Method method = lok.getClass().getMethod("getId", null);
    // System.out.println(" ##### annotations: " + Stream.of(method.getAnnotations()).map(a -> a.toString()).collect(Collectors.joining(",")));

    String json = JsonbWithIncludeVisibility.SHORT.toJson(lok);

    this.log.debug("JSON string: " + json);

    JSONAssert.assertEquals(""
        + "{\"aktiv\":" + lok.isAktiv()
        + ",\"fahrstufe\":" + lok.getFahrstufe()
        + ",\"funktionStatus\":" + lok.getFunktionStatus()
        + ",\"id\":\"" + lok.getId().getAdresse() + "@" + lok.getId().getSystemTyp().name() + "\""
        + ",\"licht\":" + lok.isLicht()
        + ",\"rueckwaerts\":" + lok.isRueckwaerts()
        + "}",
        json,
        true);
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

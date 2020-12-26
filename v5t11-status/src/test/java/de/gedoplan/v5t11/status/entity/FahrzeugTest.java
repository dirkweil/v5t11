package de.gedoplan.v5t11.status.entity;

import static org.junit.jupiter.api.Assertions.*;

import de.gedoplan.v5t11.status.StatusEventCollector;
import de.gedoplan.v5t11.status.entity.baustein.zentrale.DummyZentrale;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;
import de.gedoplan.v5t11.util.test.V5t11TestConfigDirExtension;

import javax.annotation.PostConstruct;
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

  public static final Fahrzeug lok103_003_0 = new Fahrzeug(new FahrzeugId(SystemTyp.DCC, 1103));
  public static final Fahrzeug lok210_004_8 = new Fahrzeug(new FahrzeugId(SystemTyp.SX1, 2));
  public static final Fahrzeug lok217_001_7 = new Fahrzeug(new FahrzeugId(SystemTyp.SX2, 1217));

  public static final Fahrzeug[] loks = { lok103_003_0, lok210_004_8, lok217_001_7 };

  @Inject
  Steuerung steuerung;

  @Inject
  StatusEventCollector statusEventCollector;

  @Inject
  Logger log;

  @PostConstruct
  void postConstruct() {
    for (Fahrzeug lok : loks) {
      lok.injectFields();
      this.steuerung.addFahrzeug(lok);
    }
  }

  @Test
  public void test_01_toShortJson() throws Exception {

    Fahrzeug fahrzeug = lok103_003_0;

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

    Fahrzeug lok = lok210_004_8;
    assertEquals(lok.getId().getSystemTyp(), SystemTyp.SX1, "Lok-Typ von " + lok.getId());

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

    Fahrzeug lok = lok217_001_7;
    assertEquals(SystemTyp.SX2, lok.getId().getSystemTyp(), "Lok-Typ von " + lok.getId());

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

    Fahrzeug lok = lok103_003_0;
    assertEquals(SystemTyp.DCC, lok.getId().getSystemTyp(), "Lok-Typ von " + lok.getId());

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

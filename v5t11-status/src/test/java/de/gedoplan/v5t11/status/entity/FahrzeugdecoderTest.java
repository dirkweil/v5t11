package de.gedoplan.v5t11.status.entity;

import de.gedoplan.v5t11.status.StatusEventCollector;
import de.gedoplan.v5t11.status.entity.baustein.zentrale.DummyZentrale;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeugdecoder;
import de.gedoplan.v5t11.util.domain.attribute.DecoderAdr;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbWithVisibility;
import de.gedoplan.v5t11.util.test.V5t11TestConfigDirExtension;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;
import jakarta.json.Json;

import io.quarkus.test.junit.QuarkusTestExtension;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({ V5t11TestConfigDirExtension.class, QuarkusTestExtension.class })
@TestMethodOrder(MethodOrderer.MethodName.class)
public class FahrzeugdecoderTest {

  public static final Fahrzeugdecoder lok103_003_0 = new Fahrzeugdecoder(new DecoderAdr(SystemTyp.DCC, 1103));
  public static final Fahrzeugdecoder lok210_004_8 = new Fahrzeugdecoder(new DecoderAdr(SystemTyp.SX1, 2));
  public static final Fahrzeugdecoder lok217_001_7 = new Fahrzeugdecoder(new DecoderAdr(SystemTyp.SX2, 1217));

  public static final Fahrzeugdecoder[] loks = { lok103_003_0, lok210_004_8, lok217_001_7 };

  @Inject
  Steuerung steuerung;

  @Inject
  StatusEventCollector statusEventCollector;

  @Inject
  Logger log;

  @PostConstruct
  void postConstruct() {
    for (Fahrzeugdecoder lok : loks) {
      lok.injectFields();
      this.steuerung.addFahrzeugdecoder(lok);
    }
  }

  @Test
  public void test_01_toShortJson() throws Exception {

    Fahrzeugdecoder fahrzeugdecoder = lok103_003_0;

    String json = JsonbWithVisibility.SHORT.toJson(fahrzeugdecoder);

    this.log.debug("JSON string: " + json);

    String expected = Json.createObjectBuilder()
      .add("decoderAdr", fahrzeugdecoder.getId().toString())
      .add("lastChangeMillis", fahrzeugdecoder.getLastChangeMillis())
      .add("aktiv", fahrzeugdecoder.isAktiv())
      .add("fahrstufe", fahrzeugdecoder.getFahrstufe())
      .add("licht", fahrzeugdecoder.isLicht())
      .add("rueckwaerts", fahrzeugdecoder.isRueckwaerts())
      .add("fktBits", fahrzeugdecoder.getFktBits())
      .build().toString();

    JSONAssert.assertEquals(expected, json, true);
  }

  @Test
  public void test_04_sx1_events() throws Exception {

    this.steuerung.getZentrale().setGleisspannung(true);

    Fahrzeugdecoder lok = lok210_004_8;
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

    Fahrzeugdecoder lok = lok217_001_7;
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

    Fahrzeugdecoder lok = lok103_003_0;
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

  private void assertSingleStatusEvent(Fahrzeugdecoder lok) {
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

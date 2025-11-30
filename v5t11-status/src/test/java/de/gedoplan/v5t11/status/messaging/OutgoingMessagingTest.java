package de.gedoplan.v5t11.status.messaging;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeugdecoder;
import de.gedoplan.v5t11.status.testenvironment.messaging.OutgoingHandlerMock;
import de.gedoplan.v5t11.status.testenvironment.profile.V5T11Test;
import de.gedoplan.v5t11.util.domain.attribute.DecoderAdr;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.skyscreamer.jsonassert.JSONAssert;

@QuarkusTest
@TestProfile(V5T11Test.class)
public class OutgoingMessagingTest {

  public static final Fahrzeugdecoder lok103_003_0 = new Fahrzeugdecoder(new DecoderAdr(SystemTyp.DCC, 1103));
  public static final Fahrzeugdecoder lok210_004_8 = new Fahrzeugdecoder(new DecoderAdr(SystemTyp.SX1, 2));
  public static final Fahrzeugdecoder lok217_001_7 = new Fahrzeugdecoder(new DecoderAdr(SystemTyp.SX2, 1217));

  public static final Fahrzeugdecoder[] loks = { lok103_003_0, lok210_004_8, lok217_001_7 };

  @Inject
  Steuerung steuerung;

  @Inject
  OutgoingHandlerMock outgoingHandler;

  @Inject
  Logger logger;

  @BeforeEach
  void setup(TestInfo testInfo) {
    this.logger.infof("----- %s -----", testInfo.getDisplayName());
  }

  @Test
  public void testDecoderJson() throws Exception {

    Fahrzeugdecoder fahrzeugdecoder = lok103_003_0;

    this.outgoingHandler.publish(fahrzeugdecoder);
    String json = this.outgoingHandler.getJson();

    this.logger.debug("JSON string: " + json);

    JsonObject expectedPayload = Json.createObjectBuilder()
      .add("decoderAdr", fahrzeugdecoder.getId().toString())
      .add("lastChangeMillis", fahrzeugdecoder.getLastChangeMillis())
      .add("aktiv", fahrzeugdecoder.isAktiv())
      .add("fahrstufe", fahrzeugdecoder.getFahrstufe())
      .add("licht", fahrzeugdecoder.isLicht())
      .add("rueckwaerts", fahrzeugdecoder.isRueckwaerts())
      .add("fktBits", fahrzeugdecoder.getFktBits())
      .build();
    String expected = Json.createObjectBuilder()
      .add("decoder", expectedPayload)
      .build().toString();

    JSONAssert.assertEquals(expected, json, true);
  }

  @Test
  public void testGleisJson() throws Exception {

    Gleis gleis = this.steuerung.getGleis("test", "1");

    this.outgoingHandler.publish(gleis);
    String json = this.outgoingHandler.getJson();

    this.logger.debug("JSON string: " + json);

    JsonObject expectedPayload = Json.createObjectBuilder()
      .add("key", gleis.getKey().toString())
      .add("lastChangeMillis", gleis.getLastChangeMillis())
      .add("besetzt", gleis.isBesetzt())
      .build();
    String expected = Json.createObjectBuilder()
      .add("gleis", expectedPayload)
      .build().toString();

    JSONAssert.assertEquals(expected, json, true);
  }

  @Test
  public void testSignalJson() throws Exception {

    Signal signal = this.steuerung.getSignal("test", "P2");

    this.outgoingHandler.publish(signal);
    String json = this.outgoingHandler.getJson();

    this.logger.debug("JSON string: " + json);

    JsonObject expectedPayload = Json.createObjectBuilder()
      .add("key", signal.getKey().toString())
      .add("lastChangeMillis", signal.getLastChangeMillis())
      .add("stellung", signal.getStellung().toString())
      .add("typ", signal.getTyp().toString())
      .build();
    String expected = Json.createObjectBuilder()
      .add("signal", expectedPayload)
      .build().toString();

    JSONAssert.assertEquals(expected, json, true);
  }

  @Test
  public void testWeicheJson() throws Exception {

    Weiche weiche = this.steuerung.getWeiche("test", "10");

    this.outgoingHandler.publish(weiche);
    String json = this.outgoingHandler.getJson();

    this.logger.debug("JSON string: " + json);

    JsonObject expectedPayload = Json.createObjectBuilder()
      .add("key", weiche.getKey().toString())
      .add("lastChangeMillis", weiche.getLastChangeMillis())
      .add("stellung", weiche.getStellung().toString())
      .build();
    String expected = Json.createObjectBuilder()
      .add("weiche", expectedPayload)
      .build().toString();

    JSONAssert.assertEquals(expected, json, true);
  }

  @Test
  public void testZentraleJson() throws Exception {

    Zentrale zentrale = this.steuerung.getZentrale();

    this.outgoingHandler.publish(zentrale);
    String json = this.outgoingHandler.getJson();

    this.logger.debug("JSON string: " + json);

    JsonObject expectedPayload = Json.createObjectBuilder()
      .add("gleisspannung", zentrale.isGleisspannung())
      .add("kurzschluss", zentrale.isKurzschluss())
      .build();
    String expected = Json.createObjectBuilder()
      .add("zentrale", expectedPayload)
      .build().toString();

    JSONAssert.assertEquals(expected, json, true);
  }

}

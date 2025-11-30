package de.gedoplan.v5t11.fahrstrassen.messaging;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.testenvironment.messaging.OutgoingHandlerMock;
import de.gedoplan.v5t11.fahrstrassen.testenvironment.profile.V5T11Test;

import jakarta.inject.Inject;
import jakarta.json.Json;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.skyscreamer.jsonassert.JSONAssert;

@QuarkusTest
@TestProfile(V5T11Test.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class OutgoingMessagingTest {

  private static final String FS_BEREICH = "show";
  private static final String FS_NAME = "11-W1-1-W3-S";

  @Inject
  Parcours parcours;

  @Inject
  OutgoingHandlerMock outgoingHandler;

  @Inject
  Logger log;

  @Test
  public void testFahrstrasseJson() throws Exception {

    Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse(FS_BEREICH, FS_NAME);

    this.outgoingHandler.publish(fahrstrasse);
    String json = this.outgoingHandler.getJson();

    this.log.debug("JSON string: " + json);

    String expected = Json.createObjectBuilder()
      .add("key", fahrstrasse.getKey().toString())
      .add("reservierungsTyp", fahrstrasse.getReservierungsTyp().toString())
      .add("teilFreigabeAnzahl", fahrstrasse.getTeilFreigabeAnzahl())
      .build().toString();

    JSONAssert.assertEquals(expected, json, true);
  }

}

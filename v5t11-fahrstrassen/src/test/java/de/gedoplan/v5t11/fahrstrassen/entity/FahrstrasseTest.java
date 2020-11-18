package de.gedoplan.v5t11.fahrstrassen.entity;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.FahrstrassenGeraet;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrassenelement;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;
import de.gedoplan.v5t11.util.test.V5t11TestConfigDirExtension;

import java.util.stream.Stream;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;

import io.quarkus.test.junit.QuarkusTestExtension;

@ExtendWith({ V5t11TestConfigDirExtension.class, QuarkusTestExtension.class })
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
public class FahrstrasseTest {

  private static final String FS_BEREICH = "show";
  private static final String FS_NAME = "11-W1-1-W3-S";

  @Inject
  Parcours parcours;

  @Inject
  Logger log;

  @Test
  public void test_01_toShortJson() throws Exception {

    Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse(FS_BEREICH, FS_NAME);

    String json = JsonbWithIncludeVisibility.SHORT.toJson(fahrstrasse);

    this.log.debug("JSON string: " + json);

    String expected = Json.createObjectBuilder()
        .add("bereich", fahrstrasse.getBereich())
        .add("name", fahrstrasse.getName())
        .add("reservierungsTyp", fahrstrasse.getReservierungsTyp().name())
        .add("teilFreigabeAnzahl", fahrstrasse.getTeilFreigabeAnzahl())
        .build().toString();

    JSONAssert.assertEquals(expected, json, true);
  }

  @Test
  public void test_02_toFullJson() throws Exception {

    Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse(FS_BEREICH, FS_NAME);

    String json = JsonbWithIncludeVisibility.FULL.toJson(fahrstrasse);

    this.log.debug("JSON string: " + json);

    JsonArrayBuilder elementeBuilder = Json.createArrayBuilder();
    fahrstrasse.getElemente().forEach(fe -> {
      JsonObjectBuilder elementBuilder = Json.createObjectBuilder()
          .add("bereich", fe.getBereich())
          .add("name", fe.getName())
          .add("typ", fe.getTyp())
          .add("zaehlrichtung", fe.isZaehlrichtung());
      if (fe instanceof FahrstrassenGeraet) {
        elementBuilder.add("schutz", fe.isSchutz());
        elementBuilder.add("stellung", fe.getStellung().name());
      }
      elementeBuilder.add(elementBuilder.build());
    });
    String expected = Json.createObjectBuilder()
        .add("bereich", fahrstrasse.getBereich())
        .add("name", fahrstrasse.getName())
        .add("elemente", elementeBuilder.build())
        .add("rank", fahrstrasse.getRank())
        .add("reservierungsTyp", fahrstrasse.getReservierungsTyp().name())
        .add("teilFreigabeAnzahl", fahrstrasse.getTeilFreigabeAnzahl())
        .add("zaehlrichtung", fahrstrasse.isZaehlrichtung())
        .build()
        .toString();

    JSONAssert.assertEquals(expected, json, true);
  }

  @Test
  public void test_03_reservieren() throws Exception {

    Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse(FS_BEREICH, FS_NAME);

    assertThat("Fahrstrassenreservierung zu Beginn", fahrstrasse.getReservierungsTyp(), is(FahrstrassenReservierungsTyp.UNRESERVIERT));

    try {
      // Fahrstrasse reservieren
      boolean reservierenResult = fahrstrasse.reservieren(FahrstrassenReservierungsTyp.ZUGFAHRT);
      assertThat("Reservierungsergebnis", reservierenResult, is(true));

      // Ist der FS-Status richtig?
      assertThat("Fahrstrassenreservierung nach Reservierung", fahrstrasse.getReservierungsTyp(), is(FahrstrassenReservierungsTyp.ZUGFAHRT));

      // Sind alle FS-Elemente reserviert?
      fahrstrasse.getElemente().forEach(fe -> {
        if (fe.isSchutz()) {
          assertThat("Fahrstrassenelement reserviert (d. h. der FS zugeordnet)", fe.getFahrwegelement().getReserviertefahrstrasse(), nullValue());
        } else {
          assertThat("Fahrstrassenelement reserviert (d. h. der FS zugeordnet)", fe.getFahrwegelement().getReserviertefahrstrasse(), is(fahrstrasse));
        }
      });

      // Ist die Teilfreigabeanzahl richtig?
      assertThat("Teilfreigabeanzahl", fahrstrasse.getTeilFreigabeAnzahl(), is(0));

    } finally {
      // FS wieder auf Ausgangszustand (unreserviert) setzen
      try {
        fahrstrasse.freigeben(null);
      } catch (Exception e) {
      }
    }
  }

  @Test
  public void test_04_komplettFreigeben() throws Exception {

    Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse(FS_BEREICH, FS_NAME);

    assertThat("Fahrstrassenreservierung zu Beginn", fahrstrasse.getReservierungsTyp(), is(FahrstrassenReservierungsTyp.UNRESERVIERT));

    try {
      // Fahrstrasse reservieren
      fahrstrasse.reservieren(FahrstrassenReservierungsTyp.ZUGFAHRT);

      // Fahrstrasse wieder freigeben
      boolean freigabeResult = fahrstrasse.reservieren(FahrstrassenReservierungsTyp.UNRESERVIERT);
      assertThat("Freigabeergebnis", freigabeResult, is(true));

      // Ist der FS-Status richtig?
      assertThat("Fahrstrassenreservierung nach Freigabe", fahrstrasse.getReservierungsTyp(), is(FahrstrassenReservierungsTyp.UNRESERVIERT));

      // Sind alle FS-Elemente unreserviert?
      fahrstrasse.getElemente().forEach(fe -> {
        assertThat("Fahrstrassenelement reserviert (d. h. der FS zugeordnet)", fe.getFahrwegelement().getReserviertefahrstrasse(), nullValue());
      });

      // Ist die Teilfreigabeanzahl richtig?
      assertThat("Teilfreigabeanzahl", fahrstrasse.getTeilFreigabeAnzahl(), is(fahrstrasse.getElemente().size()));

    } finally {
      // FS wieder auf Ausgangszustand (unreserviert) setzen
      try {
        fahrstrasse.freigeben(null);
      } catch (Exception e) {
      }
    }
  }

  @Test
  public void test_05_teilFreigeben() throws Exception {

    Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse(FS_BEREICH, FS_NAME);

    assertThat("Fahrstrassenreservierung zu Beginn", fahrstrasse.getReservierungsTyp(), is(FahrstrassenReservierungsTyp.UNRESERVIERT));

    try {
      // Fahrstrasse reservieren
      fahrstrasse.reservieren(FahrstrassenReservierungsTyp.ZUGFAHRT);

      Stream.of(this.parcours.getGleisabschnitt(FS_BEREICH, "11"), this.parcours.getGleisabschnitt(FS_BEREICH, "1"), this.parcours.getGleisabschnitt(FS_BEREICH, "S"))
          .forEach(g -> {
            // Fahrstrasse teilweise freigeben (aktueller Gleisabschnitt ist erstes *nicht* freigegebenes Element)
            fahrstrasse.freigeben(g);

            // Ist die Anzahl bislang freigegebener Elemente richtig?
            int teilFreigabeAnzahl = 0;
            for (Fahrstrassenelement fe : fahrstrasse.getElemente()) {
              if (g.equals(fe.getFahrwegelement())) {
                break;
              }
              ++teilFreigabeAnzahl;
            }
            assertThat("Teilfreigabeanzahl", fahrstrasse.getTeilFreigabeAnzahl(), is(teilFreigabeAnzahl));

            // Sind die teilfreigegebenen Elemente auch freigegeben?
            fahrstrasse.getElemente().stream().limit(teilFreigabeAnzahl).forEach(fe -> {
              assertThat("Fahrstrassenelement reserviert (d. h. der FS zugeordnet)", fe.getFahrwegelement().getReserviertefahrstrasse(), nullValue());
            });

            // Sind die restlichen Elemente noch reserviert?
            fahrstrasse.getElemente().stream().skip(teilFreigabeAnzahl).forEach(fe -> {
              if (fe.isSchutz()) {
                assertThat("Fahrstrassenelement reserviert (d. h. der FS zugeordnet)", fe.getFahrwegelement().getReserviertefahrstrasse(), nullValue());
              } else {
                assertThat("Fahrstrassenelement reserviert (d. h. der FS zugeordnet)", fe.getFahrwegelement().getReserviertefahrstrasse(), is(fahrstrasse));
              }
            });
          });

    } finally {
      // FS wieder auf Ausgangszustand (unreserviert) setzen
      try {
        fahrstrasse.freigeben(null);
      } catch (Exception e) {
      }
    }
  }
}

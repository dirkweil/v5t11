package de.gedoplan.v5t11.fahrstrassen.entity;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.FahrstrassenGeraet;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrassenelement;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrstrassen.persistence.GleisabschnittRepository;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;
import de.gedoplan.v5t11.util.test.V5t11TestConfigDirExtension;

import java.util.stream.Stream;

import javax.inject.Inject;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.transaction.Transactional;

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
        // TODO wird das Kurz-JSON gebraucht?
        // .add("reservierungsTyp", fahrstrasse.getReservierungsTyp().name())
        // .add("teilFreigabeAnzahl", fahrstrasse.getTeilFreigabeAnzahl())
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
        // TODO wird das Full-JSON gebraucht?
        // .add("reservierungsTyp", fahrstrasse.getReservierungsTyp().name())
        // .add("teilFreigabeAnzahl", fahrstrasse.getTeilFreigabeAnzahl())
        .add("zaehlrichtung", fahrstrasse.isZaehlrichtung())
        .build()
        .toString();

    JSONAssert.assertEquals(expected, json, true);
  }

  @Test
  @Transactional
  public void test_03_reservieren() throws Exception {

    Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse(FS_BEREICH, FS_NAME);

    assertThat("Fahrstrassenreservierung zu Beginn", fahrstrasse.getFahrstrassenStatus().getReservierungsTyp(), is(FahrstrassenReservierungsTyp.UNRESERVIERT));

    try {
      // Fahrstrasse reservieren
      boolean reservierenResult = fahrstrasse.reservieren(FahrstrassenReservierungsTyp.ZUGFAHRT);
      assertThat("Reservierungsergebnis", reservierenResult, is(true));

      // Ist der FS-Status richtig?
      assertThat("Fahrstrassenreservierung nach Reservierung", fahrstrasse.getFahrstrassenStatus().getReservierungsTyp(), is(FahrstrassenReservierungsTyp.ZUGFAHRT));

      // Sind alle FS-Elemente reserviert?
      fahrstrasse.getElemente().forEach(fe -> {
        String reason = String.format("Zuordnung des FS-Elements %s", fe.getCode());
        if (fe.isSchutz()) {
          assertThat(reason, fe.getFahrwegelement().getReserviertefahrstrasseId(), nullValue());
        } else {
          assertThat(reason, fe.getFahrwegelement().getReserviertefahrstrasseId(), is(fahrstrasse.getId()));
        }
      });

      // Ist die Teilfreigabeanzahl richtig?
      assertThat("Teilfreigabeanzahl", fahrstrasse.getFahrstrassenStatus().getTeilFreigabeAnzahl(), is(0));

    } finally {
      // FS wieder auf Ausgangszustand (unreserviert) setzen
      try {
        fahrstrasse.freigeben(null);
      } catch (Exception e) {
      }
    }
  }

  @Test
  @Transactional
  public void test_04_komplettFreigeben() throws Exception {

    Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse(FS_BEREICH, FS_NAME);

    assertThat("Fahrstrassenreservierung zu Beginn", fahrstrasse.getFahrstrassenStatus().getReservierungsTyp(), is(FahrstrassenReservierungsTyp.UNRESERVIERT));

    try {
      // Fahrstrasse reservieren
      boolean reservierenResult = fahrstrasse.reservieren(FahrstrassenReservierungsTyp.ZUGFAHRT);
      assertThat("Reservierungsergebnis", reservierenResult, is(true));

      // Fahrstrasse wieder freigeben
      boolean freigabeResult = fahrstrasse.reservieren(FahrstrassenReservierungsTyp.UNRESERVIERT);
      assertThat("Freigabeergebnis", freigabeResult, is(true));

      // Ist der FS-Status richtig?
      assertThat("Fahrstrassenreservierung nach Freigabe", fahrstrasse.getFahrstrassenStatus().getReservierungsTyp(), is(FahrstrassenReservierungsTyp.UNRESERVIERT));

      // Sind alle FS-Elemente unreserviert?
      fahrstrasse.getElemente().forEach(fe -> {
        String reason = String.format("Zuordnung des FS-Elements %s", fe.getCode());
        assertThat(reason, fe.getFahrwegelement().getReserviertefahrstrasseId(), nullValue());
      });

      // Ist die Teilfreigabeanzahl richtig?
      assertThat("Teilfreigabeanzahl", fahrstrasse.getFahrstrassenStatus().getTeilFreigabeAnzahl(), is(fahrstrasse.getElemente().size()));

    } finally {
      // FS wieder auf Ausgangszustand (unreserviert) setzen
      try {
        fahrstrasse.freigeben(null);
      } catch (Exception e) {
      }
    }
  }

  @Inject
  GleisabschnittRepository gleisabschnittRepository;

  @Test
  @Transactional
  public void test_05_teilFreigeben() throws Exception {

    Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse(FS_BEREICH, FS_NAME);
    assertNotNull(fahrstrasse);

    Gleisabschnitt gleisabschnitt11 = this.gleisabschnittRepository.findById(new BereichselementId(FS_BEREICH, "11"));
    assertNotNull(gleisabschnitt11);
    Gleisabschnitt gleisabschnitt1 = this.gleisabschnittRepository.findById(new BereichselementId(FS_BEREICH, "1"));
    assertNotNull(gleisabschnitt1);
    Gleisabschnitt gleisabschnittS = this.gleisabschnittRepository.findById(new BereichselementId(FS_BEREICH, "S"));
    assertNotNull(gleisabschnittS);

    assertThat("Fahrstrassenreservierung zu Beginn", fahrstrasse.getFahrstrassenStatus().getReservierungsTyp(), is(FahrstrassenReservierungsTyp.UNRESERVIERT));

    try {
      // Fahrstrasse reservieren
      boolean reservierenResult = fahrstrasse.reservieren(FahrstrassenReservierungsTyp.ZUGFAHRT);
      assertThat("Reservierungsergebnis", reservierenResult, is(true));

      Stream.of(gleisabschnitt11, gleisabschnitt1, gleisabschnittS)
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
            assertThat("Teilfreigabeanzahl", fahrstrasse.getFahrstrassenStatus().getTeilFreigabeAnzahl(), is(teilFreigabeAnzahl));

            // Sind die teilfreigegebenen Elemente auch freigegeben?
            fahrstrasse.getElemente().stream().limit(teilFreigabeAnzahl).forEach(fe -> {
              assertThat("Fahrstrassenelement reserviert (d. h. der FS zugeordnet)", fe.getFahrwegelement().getReserviertefahrstrasseId(), nullValue());
            });

            // Sind die restlichen Elemente noch reserviert?
            fahrstrasse.getElemente().stream().skip(teilFreigabeAnzahl).forEach(fe -> {
              if (fe.isSchutz()) {
                assertThat("Fahrstrassenelement reserviert (d. h. der FS zugeordnet)", fe.getFahrwegelement().getReserviertefahrstrasseId(), nullValue());
              } else {
                assertThat("Fahrstrassenelement reserviert (d. h. der FS zugeordnet)", fe.getFahrwegelement().getReserviertefahrstrasseId(), is(fahrstrasse.getId()));
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

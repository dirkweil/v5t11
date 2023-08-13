package de.gedoplan.v5t11.fahrstrassen.entity;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.FahrstrassenGeraet;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.FahrstrassenSignal;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.FahrstrassenWeiche;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrassenelement;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrstrassen.persistence.GleisRepository;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;
import de.gedoplan.v5t11.util.test.V5t11TestConfigDirExtension;

import java.util.stream.Stream;

import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;
import jakarta.json.JsonObjectBuilder;
import jakarta.transaction.Transactional;

import io.quarkus.test.junit.QuarkusTestExtension;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;
import org.skyscreamer.jsonassert.JSONAssert;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith({ V5t11TestConfigDirExtension.class, QuarkusTestExtension.class })
@TestMethodOrder(MethodOrderer.MethodName.class)
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
      .add("key", fahrstrasse.getKey().toString())
      .add("reservierungsTyp", fahrstrasse.getReservierungsTyp().toString())
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
        .add("key", fe.getKey().toString())
        .add("typ", fe.getTyp().toString())
        .add("zaehlrichtung", fe.isZaehlrichtung());
      if (fe instanceof FahrstrassenGeraet) {
        elementBuilder.add("schutz", fe.isSchutz());
        String code = "?";
        if (fe instanceof FahrstrassenSignal) {
          code = ((FahrstrassenSignal) fe).getStellung().toString();
        } else if (fe instanceof FahrstrassenWeiche) {
          code = ((FahrstrassenWeiche) fe).getStellung().toString();
          Integer limit = ((FahrstrassenWeiche) fe).getLimit();
          if (limit != null) {
            elementBuilder.add("limit", limit);
          }
        }
        elementBuilder.add("stellung", code);
      }
      elementeBuilder.add(elementBuilder.build());
    });
    String expected = Json.createObjectBuilder()
      .add("key", fahrstrasse.getKey().toString())
      .add("elemente", elementeBuilder.build())
      .add("rank", fahrstrasse.getRank())
      .add("reservierungsTyp", fahrstrasse.getReservierungsTyp().toString())
      .add("teilFreigabeAnzahl", fahrstrasse.getTeilFreigabeAnzahl())
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
        String reason = String.format("Zuordnung des FS-Elements %s", fe.toString());
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
        String reason = String.format("Zuordnung des FS-Elements %s", fe.toString());
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
  GleisRepository gleisRepository;

  @Test
  @Transactional
  public void test_05_teilFreigeben() throws Exception {

    Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse(FS_BEREICH, FS_NAME);
    assertNotNull(fahrstrasse);

    Gleis gleis11 = this.gleisRepository.findById(new BereichselementId(FS_BEREICH, "11")).orElse(null);
    assertNotNull(gleis11);
    Gleis gleis1 = this.gleisRepository.findById(new BereichselementId(FS_BEREICH, "1")).orElse(null);
    assertNotNull(gleis1);
    Gleis gleisS = this.gleisRepository.findById(new BereichselementId(FS_BEREICH, "S")).orElse(null);
    assertNotNull(gleisS);

    assertThat("Fahrstrassenreservierung zu Beginn", fahrstrasse.getFahrstrassenStatus().getReservierungsTyp(), is(FahrstrassenReservierungsTyp.UNRESERVIERT));

    try {
      // Fahrstrasse reservieren
      boolean reservierenResult = fahrstrasse.reservieren(FahrstrassenReservierungsTyp.ZUGFAHRT);
      assertThat("Reservierungsergebnis", reservierenResult, is(true));

      Stream.of(gleis11, gleis1, gleisS)
        .forEach(g -> {
          // Fahrstrasse teilweise freigeben (aktueller Gleis ist erstes *nicht* freigegebenes Element)
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

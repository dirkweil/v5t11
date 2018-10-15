package de.gedoplan.v5t11.fahrstrassen.entity;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import de.gedoplan.v5t11.fahrstrassen.TestBase;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrassenelement;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import java.util.stream.Stream;

import javax.inject.Inject;

import org.apache.meecrowave.junit.MonoMeecrowave;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.skyscreamer.jsonassert.JSONAssert;

@RunWith(MonoMeecrowave.Runner.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class FahrstrasseTest extends TestBase {

  private static final String FS_BEREICH = "show";
  private static final String FS_NAME = "11-W1-1-W3-S";

  @Inject
  Parcours parcours;

  @Test
  public void test_01_toShortJson() throws Exception {

    Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse(FS_BEREICH, FS_NAME);

    String json = JsonbWithIncludeVisibility.SHORT.toJson(fahrstrasse);

    this.log.debug("JSON string: " + json);

    assertThat(json, is("{"
        + "\"bereich\":\"show\""
        + ",\"name\":\"11-W1-1-W3-S\""
        + ",\"reservierungsTyp\":\"" + fahrstrasse.getReservierungsTyp() + "\""
        + ",\"teilFreigabeAnzahl\":" + fahrstrasse.getTeilFreigabeAnzahl()
        + "}"));
  }

  @Test
  public void test_02_toFullJson() throws Exception {

    Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse(FS_BEREICH, FS_NAME);

    String json = JsonbWithIncludeVisibility.FULL.toJson(fahrstrasse);

    this.log.debug("JSON string: " + json);

    JSONAssert.assertEquals(""
        + "{"
        + "\"bereich\":\"show\""
        + ",\"elemente\":[{\"bereich\":\"show\",\"name\":\"11\",\"typ\":\"GLEIS\",\"zaehlrichtung\":true},{\"bereich\":\"show\",\"name\":\"1\",\"schutz\":false,\"stellung\":\"ABZWEIGEND\",\"typ\":\"WEICHE\",\"zaehlrichtung\":true},{\"bereich\":\"show\",\"name\":\"W1\",\"typ\":\"GLEIS\",\"zaehlrichtung\":true},{\"bereich\":\"show\",\"name\":\"2\",\"schutz\":true,\"stellung\":\"GERADE\",\"typ\":\"WEICHE\",\"zaehlrichtung\":true},{\"bereich\":\"show\",\"name\":\"1\",\"typ\":\"GLEIS\",\"zaehlrichtung\":true},{\"bereich\":\"show\",\"name\":\"N1\",\"schutz\":false,\"stellung\":\"LANGSAMFAHRT\",\"typ\":\"SIGNAL\",\"zaehlrichtung\":true},{\"bereich\":\"show\",\"name\":\"3\",\"schutz\":false,\"stellung\":\"ABZWEIGEND\",\"typ\":\"WEICHE\",\"zaehlrichtung\":true},{\"bereich\":\"show\",\"name\":\"W3\",\"typ\":\"GLEIS\",\"zaehlrichtung\":true},{\"bereich\":\"show\",\"name\":\"N2\",\"schutz\":true,\"stellung\":\"HALT\",\"typ\":\"SIGNAL\",\"zaehlrichtung\":true},{\"bereich\":\"show\",\"name\":\"S\",\"typ\":\"GLEIS\",\"zaehlrichtung\":true}]"
        + ",\"name\":\"11-W1-1-W3-S\""
        + ",\"rank\":" + fahrstrasse.getRank()
        + ",\"reservierungsTyp\":\"" + fahrstrasse.getReservierungsTyp() + "\""
        + ",\"teilFreigabeAnzahl\":" + fahrstrasse.getTeilFreigabeAnzahl()
        + ",\"zaehlrichtung\":" + fahrstrasse.isZaehlrichtung()
        + "}",
        json,
        true);
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
      } catch (Exception e) {}
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
      } catch (Exception e) {}
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
      } catch (Exception e) {}
    }
  }
}

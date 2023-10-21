package de.gedoplan.v5t11.fahrzeuge.entity;

import de.gedoplan.baselibs.utils.xml.XmlConverter;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug.FahrzeugFunktion;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugKonfiguration;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import jakarta.json.Json;
import jakarta.json.JsonArrayBuilder;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.skyscreamer.jsonassert.JSONAssert;

import static de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF;
import static de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA;
import static de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG;
import static de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FG;
import static de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL;

//@ExtendWith({ V5t11TestConfigDirExtension.class, QuarkusTestExtension.class })
@TestMethodOrder(MethodOrderer.MethodName.class)
public class FahrzeugTest {

  public static final Fahrzeug lok112_491_6 = Fahrzeug.builder()
    .betriebsnummer("112 491-6")
    .systemTyp(SystemTyp.DCC)
    .adresse(1112)
    .funktion(new FahrzeugFunktion(FG, 1, false, false, false, "Motor"))
    .funktion(new FahrzeugFunktion(BG, 2, true, false, false, "Pfeife lang"))
    .funktion(new FahrzeugFunktion(BA, 3, true, false, false, "Schaffnerpfiff"))
    .funktion(new FahrzeugFunktion(BG, 4, true, false, false, "Kompressor"))
    .funktion(new FahrzeugFunktion(BG, 5, true, false, false, "Ankuppeln"))
    .funktion(new FahrzeugFunktion(AF, 6, false, false, false, "Direktsteuerung"))
    .funktion(new FahrzeugFunktion(BA, 7, true, false, false, "???"))
    .funktion(new FahrzeugFunktion(BG, 8, true, false, false, "Sanden"))
    .funktion(new FahrzeugFunktion(AF, 9, false, false, false, "Rangiergang"))
    .funktion(new FahrzeugFunktion(AF, 10, false, false, true, "Geräusche ausblenden"))
    .funktion(new FahrzeugFunktion(BG, 13, true, true, false, "Pfeife kurz"))
    .funktion(new FahrzeugFunktion(BG, 14, true, false, false, "Kurvenquietschen"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_1100_0000_0000, 0b0000_0000_0000_0000, false, false, false, "beidseitig"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_1100_0000_0000, 0b0000_0100_0000_0000, false, false, false, "nur Seite 1"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_1100_0000_0000, 0b0000_1000_0000_0000, false, false, false, "nur Seite 2"))
    .konfiguration(new FahrzeugKonfiguration(17, 192, "Höherwertiger Teil der langen Adresse"))
    .konfiguration(new FahrzeugKonfiguration(18, 0, "Niederwertiger Teil der langen Adresse"))
    .konfiguration(new FahrzeugKonfiguration(29, 46, """
      Konfigurationsregister
      Bit 0: Richtung umkehren
      Bit 1: 28/126 Fahrstufen (statt 14)
      Bit 2: Analogbetrieb erlaubt
      Bit 3: Rückmeldung erlaubt
      Bit 5: Lange Lokadresse nach CV17/18"""))
    .build();

  public static final Fahrzeug[] loks = { lok112_491_6 };

  //  @Inject
  //  Logger log;

  @Test
  public void test_01_toShortJson() throws Exception {

    Fahrzeug fahrzeug = lok112_491_6;

    String json = JsonbWithIncludeVisibility.SHORT.toJson(fahrzeug);

    System.out.println("JSON string: " + json);

    String expected = Json.createObjectBuilder()
      .add("id", fahrzeug.getId().toString())
      .add("betriebsnummer", fahrzeug.getBetriebsnummer())
      .add("removed", fahrzeug.isRemoved())
      .build().toString();

    JSONAssert.assertEquals(expected, json, true);
  }

  @Test
  public void test_02_toFullJson() throws Exception {

    Fahrzeug fahrzeug = lok112_491_6;

    String json = JsonbWithIncludeVisibility.FULL.toJson(fahrzeug);

    System.out.println("JSON string: " + json);

    JsonArrayBuilder funktionenBuilder = Json.createArrayBuilder();
    fahrzeug.getFunktionen().forEach(f -> {
      funktionenBuilder.add(Json.createObjectBuilder()
        .add("gruppe", f.getGruppe().name())
        .add("beschreibung", f.getBeschreibung())
        .add("maske", f.getMaske())
        .add("wert", f.getWert())
        .add("impuls", f.isImpuls())
        .add("horn", f.isHorn())
        .add("fader", f.isFader())
        .build());
    });

    JsonArrayBuilder konfigurationenBuilder = Json.createArrayBuilder();
    fahrzeug.getKonfigurationen().forEach(k -> {
      konfigurationenBuilder.add(Json.createObjectBuilder()
        .add("nr", k.getNr())
        .add("beschreibung", k.getBeschreibung())
        .add("soll", k.getSoll())
        .build());
    });

    String expected = Json.createObjectBuilder()
      .add("id", fahrzeug.getId().toString())
      .add("betriebsnummer", fahrzeug.getBetriebsnummer())
      .add("removed", fahrzeug.isRemoved())
      .add("aktiv", fahrzeug.isAktiv())
      .add("fahrstufe", fahrzeug.getFahrstufe())
      .add("fktBits", fahrzeug.getFktBits())
      .add("lastChangeMillis", fahrzeug.getLastChangeMillis())
      .add("licht", fahrzeug.isLicht())
      .add("rueckwaerts", fahrzeug.isRueckwaerts())
      .add("funktionen", funktionenBuilder.build())
      .add("konfigurationen", konfigurationenBuilder.build())
      .build().toString();

    JSONAssert.assertEquals(expected, json, true);
  }

  @Test
  public void test_03_toXml() throws Exception {

    Fahrzeug fahrzeug = lok112_491_6;

    String xmlString = XmlConverter.toXml(fahrzeug);
    System.out.println("XML string: " + xmlString);

  }
}

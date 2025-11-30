package de.gedoplan.v5t11.fahrzeuge.entity;

import de.gedoplan.baselibs.utils.xml.XmlConverter;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugKonfiguration;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugTyp;
import de.gedoplan.v5t11.fahrzeuge.testenvironment.profile.V5T11Test;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;

import java.io.StringReader;
import java.util.stream.Stream;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG;
import static de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FG;
import static org.junit.jupiter.api.Assertions.assertEquals;

@QuarkusTest
@TestProfile(V5T11Test.class)
public class FahrzeugTest {

  private static final Fahrzeug lok112_491_6 = Fahrzeug.builder()
    .betriebsnummer("112 491-6")
    .fahrzeugTyp(FahrzeugTyp.LOK)
    .beschreibung("Schnellzuglok")
    .systemTyp(SystemTyp.DCC)
    .adresse(1112)
    .decoderName("Zimo 646")
    .funktion(new FahrzeugFunktion(FG, 1, false, false, false, "Motor"))
    .funktion(new FahrzeugFunktion(BG, 2, true, false, false, "Pfeife lang"))
    .konfiguration(new FahrzeugKonfiguration(17, 192, "Höherwertiger Teil der langen Adresse"))
    .konfiguration(new FahrzeugKonfiguration(18, 0, "Niederwertiger Teil der langen Adresse"))
    .build();

  // XML passend zum o.a. Fahrzeug
  private static final String lok112_491_6XmlString = """
    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <fahrzeug>
        <betriebsnummer>112 491-6</betriebsnummer>
        <fahrzeugTyp>LOK</fahrzeugTyp>
        <beschreibung>Schnellzuglok</beschreibung>
        <fahrzeugdecoder>
            <decoderName>Zimo 646</decoderName>
            <decoderAdr>1112@DCC</decoderAdr>
            <funktion maske="1" wert="1">
                <gruppe>FG</gruppe>
                <beschreibung>Motor</beschreibung>
                <impuls>false</impuls>
                <horn>false</horn>
                <fader>false</fader>
            </funktion>
            <funktion maske="2" wert="2">
                <gruppe>BG</gruppe>
                <beschreibung>Pfeife lang</beschreibung>
                <impuls>true</impuls>
                <horn>false</horn>
                <fader>false</fader>
            </funktion>
            <konfiguration nr="17" soll="192">
                <beschreibung>Höherwertiger Teil der langen Adresse</beschreibung>
            </konfiguration>
            <konfiguration nr="18" soll="0">
                <beschreibung>Niederwertiger Teil der langen Adresse</beschreibung>
            </konfiguration>
        </fahrzeugdecoder>
    </fahrzeug>
    """;

  // XML in vorletzter Form (kein Element für Fahrzeugdecoder)
  private static final String lok112_491_6XmlString_2 = """
    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <fahrzeug>
        <betriebsnummer>112 491-6</betriebsnummer>
        <fahrzeugTyp>LOK</fahrzeugTyp>
        <beschreibung>Schnellzuglok</beschreibung>
        <decoderName>Zimo MX648</decoderName>
        <decoderAdr>1112@DCC</decoderAdr>
        <funktion maske="1" wert="1">
            <gruppe>FG</gruppe>
            <beschreibung>Motor</beschreibung>
            <impuls>false</impuls>
            <horn>false</horn>
            <fader>false</fader>
        </funktion>
        <funktion maske="2" wert="2">
            <gruppe>BG</gruppe>
            <beschreibung>Pfeife lang</beschreibung>
            <impuls>true</impuls>
            <horn>false</horn>
            <fader>false</fader>
        </funktion>
        <konfiguration nr="17" soll="192">
            <beschreibung>Höherwertiger Teil der langen Adresse</beschreibung>
        </konfiguration>
        <konfiguration nr="18" soll="0">
            <beschreibung>Niederwertiger Teil der langen Adresse</beschreibung>
        </konfiguration>
    </fahrzeug>
    """;

  // XML in vorvorletzter Form (kein Element für Fahrzeugdecoder, id statt decoderAdr, decoder statt decoderName)
  private static final String lok112_491_6XmlString_3 = """
    <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
    <fahrzeug>
        <betriebsnummer>112 491-6</betriebsnummer>
        <fahrzeugTyp>LOK</fahrzeugTyp>
        <beschreibung>Schnellzuglok</beschreibung>
        <decoder>Zimo MX648</decoder>
        <id>1112@DCC</id>
        <funktion maske="1" wert="1">
            <gruppe>FG</gruppe>
            <beschreibung>Motor</beschreibung>
            <impuls>false</impuls>
            <horn>false</horn>
            <fader>false</fader>
        </funktion>
        <funktion maske="2" wert="2">
            <gruppe>BG</gruppe>
            <beschreibung>Pfeife lang</beschreibung>
            <impuls>true</impuls>
            <horn>false</horn>
            <fader>false</fader>
        </funktion>
        <konfiguration nr="17" soll="192">
            <beschreibung>Höherwertiger Teil der langen Adresse</beschreibung>
        </konfiguration>
        <konfiguration nr="18" soll="0">
            <beschreibung>Niederwertiger Teil der langen Adresse</beschreibung>
        </konfiguration>
    </fahrzeug>
    """;

  @Inject
  Logger logger;

  @BeforeEach
  void setup(TestInfo testInfo) {
    this.logger.infof("----- %s -----", testInfo.getDisplayName());
  }

  @Test
  public void testToXml() throws Exception {

    String xmlString = XmlConverter.toXml(lok112_491_6);
    this.logger.debugf("XML string:\n%s", xmlString);

    assertEquals(lok112_491_6XmlString, xmlString);
  }

  @ParameterizedTest
  @MethodSource("supplyXmlStrings")
  public void testFromXml(String version, String xmlString) throws Exception {
    Fahrzeug fahrzeug = XmlConverter.fromXml(Fahrzeug.class, new StringReader(xmlString));
    this.logger.debugf("fahrzeug: %s", fahrzeug);

    assertEquals(lok112_491_6, fahrzeug);
    assertEquals(lok112_491_6.getBetriebsnummer(), fahrzeug.getBetriebsnummer());
    assertEquals(lok112_491_6.getFahrzeugdecoder().getDecoderAdr(), fahrzeug.getFahrzeugdecoder().getDecoderAdr());
  }

  private static Stream<Arguments> supplyXmlStrings() {
    return Stream.of(
      Arguments.of("aktuell", lok112_491_6XmlString),
      Arguments.of("alt (Decoder* auf Top-Level)", lok112_491_6XmlString_2),
      Arguments.of("alt (id/decoder auf Top-Level)", lok112_491_6XmlString_3)
    );
  }
}

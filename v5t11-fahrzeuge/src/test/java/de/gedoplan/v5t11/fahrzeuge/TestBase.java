package de.gedoplan.v5t11.fahrzeuge;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;

import jakarta.inject.Inject;

import org.junit.jupiter.api.BeforeEach;

import static de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF;
import static de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA;
import static de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG;
import static de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FG;
import static de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL;

public class TestBase {

  @Inject
  FahrzeugRepository fahrzeugRepository;

  static boolean dbFilled = false;

  @BeforeEach
  void fillDb() {
    if (!dbFilled) {
      if (this.fahrzeugRepository.countAll() != fahrzeuge.length) {
        for (Fahrzeug f : fahrzeuge) {
          if (this.fahrzeugRepository.findById(f.getId()) == null) {
            this.fahrzeugRepository.persist(f);
          }
        }
      }
      dbFilled = true;
    }
  }

  public static final Fahrzeug lok103_003_0 = Fahrzeug.builder()
    .betriebsnummer("103 003-0")
    .systemTyp(SystemTyp.DCC)
    .adresse(1103)
    .funktion(new FahrzeugFunktion(AF, 1, false, false, false, "Maschinenraumbeleuchtung"))
    .funktion(new FahrzeugFunktion(FG, 2, false, false, false, "Motor"))
    .funktion(new FahrzeugFunktion(BG, 3, true, true, false, "Pfeife"))
    .funktion(new FahrzeugFunktion(AF, 4, false, false, false, "Direktsteuerung"))
    .funktion(new FahrzeugFunktion(AF, 5, false, false, false, "Führerstandsbeleuchtung"))
    .funktion(new FahrzeugFunktion(BA, 7, true, false, false, "Schaffnerpfiff"))
    .funktion(new FahrzeugFunktion(BG, 9, false, false, false, "Bremsenquietschen aus"))
    .funktion(new FahrzeugFunktion(BA, 10, true, false, false, "Bitte einsteigen ..."))
    .funktion(new FahrzeugFunktion(BG, 11, false, false, false, "Lüfter"))
    .funktion(new FahrzeugFunktion(BG, 12, true, false, false, "Sanden"))
    .funktion(new FahrzeugFunktion(BA, 13, true, false, false, "... an Gleis 1 fährt ein Zug durch ..."))
    .funktion(new FahrzeugFunktion(BA, 14, true, false, false, "Betriebsgefahr ..."))
    .funktion(new FahrzeugFunktion(BG, 15, false, false, false, "Lüfter2"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000, false, false, false, "beidseitig"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000, false, false, false, "nur Seite 1"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000, false, false, false, "nur Seite 2"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000, false, false, false, "Doppel-A"))
    .build();

  public static final Fahrzeug lok110_389_4 = Fahrzeug.builder()
    .betriebsnummer("110 389-4")
    .systemTyp(SystemTyp.DCC)
    .adresse(1110)
    .funktion(new FahrzeugFunktion(BG, 1, true, false, false, "Pfeife lang"))
    .funktion(new FahrzeugFunktion(FG, 2, false, false, false, "Motor"))
    .funktion(new FahrzeugFunktion(BG, 3, true, true, false, "Pfeife kurz"))
    .funktion(new FahrzeugFunktion(AF, 4, false, false, false, "Direktsteuerung"))
    .funktion(new FahrzeugFunktion(BG, 5, false, false, false, "Bremsenquietschen aus"))
    .funktion(new FahrzeugFunktion(BA, 7, true, false, false, "Schaffnerpfiff"))
    .funktion(new FahrzeugFunktion(BA, 9, true, false, false, "Türen schließen"))
    .funktion(new FahrzeugFunktion(BA, 10, true, false, false, "???"))
    .funktion(new FahrzeugFunktion(BG, 11, false, false, false, "Kompressor"))
    .funktion(new FahrzeugFunktion(BG, 12, true, false, false, "Sanden"))
    .funktion(new FahrzeugFunktion(BG, 13, true, false, false, "Kabinenfunk"))
    .funktion(new FahrzeugFunktion(BG, 14, true, false, false, "Lüfter"))
    .funktion(new FahrzeugFunktion(BG, 15, false, false, false, "Türenschließen"))
    .funktion(new FahrzeugFunktion(AF, 16, false, false, true, "Geräusche ausblenden"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000, false, false, false, "beidseitig"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000, false, false, false, "nur Seite 1"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000, false, false, false, "nur Seite 2"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000, false, false, false, "Doppel-A"))
    .build();

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
    .build();

  public static final Fahrzeug lok151_032_0 = Fahrzeug.builder()
    .betriebsnummer("151 032-0")
    .decoder("DHL100")
    .systemTyp(SystemTyp.SX1)
    .adresse(20)
    .build();

  public static final Fahrzeug lok194_183_0 = Fahrzeug.builder()
    .betriebsnummer("194 183-0")
    .decoder("Zimo")
    .systemTyp(SystemTyp.DCC)
    .adresse(1194)
    .funktion(new FahrzeugFunktion(FG, 1, false, false, false, "Motor"))
    .funktion(new FahrzeugFunktion(BG, 2, true, true, false, "Pfeife kurz"))
    .funktion(new FahrzeugFunktion(BG, 3, true, false, false, "Pfeife lang"))
    .funktion(new FahrzeugFunktion(BA, 4, true, false, false, "Schaffnerpfiff"))
    .funktion(new FahrzeugFunktion(BG, 5, true, false, false, "Ankuppeln"))
    .funktion(new FahrzeugFunktion(AF, 6, false, false, false, "Direktsteuerung"))
    .funktion(new FahrzeugFunktion(BG, 7, false, false, false, "Kurvenquietschen"))
    .funktion(new FahrzeugFunktion(BG, 8, false, false, false, "Handpumpe"))
    .funktion(new FahrzeugFunktion(AF, 9, false, false, false, "Rangiergang"))
    .funktion(new FahrzeugFunktion(BG, 10, false, false, false, "Lüfter"))
    .funktion(new FahrzeugFunktion(BG, 11, false, false, false, "Kompressor"))
    .funktion(new FahrzeugFunktion(BG, 12, false, false, false, "Tür"))
    .funktion(new FahrzeugFunktion(AF, 14, false, false, true, "Geräusche ausblenden"))
    .funktion(new FahrzeugFunktion(BG, 15, true, false, false, "Sanden"))
    .build();

  public static final Fahrzeug lok217_001_7 = Fahrzeug.builder()
    .betriebsnummer("217 001-7")
    .systemTyp(SystemTyp.SX2)
    .adresse(1217)
    .funktion(new FahrzeugFunktion(AF, 1, false, false, false, "Führerstandsbeleuchtung"))
    .funktion(new FahrzeugFunktion(FG, 2, false, false, false, "Motor"))
    .funktion(new FahrzeugFunktion(BG, 3, true, true, false, "Signalhorn hoch"))
    .funktion(new FahrzeugFunktion(AF, 4, false, false, false, "Direktsteuerung"))
    .funktion(new FahrzeugFunktion(BG, 5, false, false, false, "Bremsenquietschen aus"))
    .funktion(new FahrzeugFunktion(BG, 7, true, false, false, "Signalhorn tief"))
    .funktion(new FahrzeugFunktion(BG, 9, true, false, false, "Hilfsdiesel"))
    .funktion(new FahrzeugFunktion(BG, 10, true, false, false, "Kompressor"))
    .funktion(new FahrzeugFunktion(BA, 11, true, false, false, "Schaffnerpfiff"))
    .funktion(new FahrzeugFunktion(BA, 12, true, false, false, "... an Gleis 1 fährt ein Zug durch ..."))
    .funktion(new FahrzeugFunktion(BG, 13, true, false, false, "Sanden"))
    .funktion(new FahrzeugFunktion(BA, 14, true, false, false, "Bitte einsteigen ..."))
    .funktion(new FahrzeugFunktion(BG, 15, false, false, true, "Geräusche ausblenden"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000, false, false, false, "beidseitig"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000, false, false, false, "nur Seite 1"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000, false, false, false, "nur Seite 2"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000, false, false, false, "Doppel-A"))
    .build();

  public static final Fahrzeug lok217_014_0 = Fahrzeug.builder()
    .betriebsnummer("217 014-0")
    .decoder("DH14B+Sound")
    .systemTyp(SystemTyp.SX2)
    .adresse(2217)
    .funktion(new FahrzeugFunktion(AF, 1, false, false, false, "Führerstandsbeleuchtung"))
    .funktion(new FahrzeugFunktion(FG, 2, false, false, false, "Motor"))
    .funktion(new FahrzeugFunktion(BG, 3, true, true, false, "Signalhorn hoch"))
    .funktion(new FahrzeugFunktion(AF, 4, false, false, false, "Direktsteuerung"))
    .funktion(new FahrzeugFunktion(BG, 5, false, false, false, "Bremsenquietschen aus"))
    .funktion(new FahrzeugFunktion(BG, 7, true, false, false, "Signalhorn tief"))
    .funktion(new FahrzeugFunktion(BG, 9, true, false, false, "Hilfsdiesel"))
    .funktion(new FahrzeugFunktion(BG, 10, true, false, false, "Kompressor"))
    .funktion(new FahrzeugFunktion(BA, 11, true, false, false, "Schaffnerpfiff"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000, false, false, false, "beidseitig"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000, false, false, false, "nur Seite 1"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000, false, false, false, "nur Seite 2"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000, false, false, false, "Doppel-A"))
    .build();

  public static final Fahrzeug lok230_001_0 = Fahrzeug.builder()
    .betriebsnummer("230 001-0")
    .systemTyp(SystemTyp.DCC)
    .adresse(1230)
    .funktion(new FahrzeugFunktion(BG, 1, true, true, false, "Horn kurz"))
    .funktion(new FahrzeugFunktion(FG, 2, false, false, false, "Motor"))
    .funktion(new FahrzeugFunktion(BG, 3, true, false, false, "Horn lang"))
    .funktion(new FahrzeugFunktion(AF, 4, false, false, false, "Direktsteuerung"))
    .funktion(new FahrzeugFunktion(BG, 5, true, false, false, "Druckluft ablassen"))
    .funktion(new FahrzeugFunktion(BG, 7, false, false, false, "Bremsenquietschen aus"))
    .funktion(new FahrzeugFunktion(BG, 9, true, false, false, "Sanden"))
    .funktion(new FahrzeugFunktion(BA, 10, true, false, false, "Schaffnerpfiff"))
    .funktion(new FahrzeugFunktion(BA, 11, true, false, false, "???"))
    .funktion(new FahrzeugFunktion(BG, 12, true, false, false, "Türen schließen"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000, false, false, false, "beidseitig"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000, false, false, false, "nur Seite 2"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000, false, false, false, "nur Seite 1"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000, false, false, false, "Doppel-A"))
    .build();

  public static final Fahrzeug lok332_262_5 = Fahrzeug.builder()
    .betriebsnummer("332 262-5")
    .decoder("DHL100")
    .systemTyp(SystemTyp.SX1)
    .adresse(14)
    .build();

  public static final Fahrzeug lok430_119_8 = Fahrzeug.builder()
    .betriebsnummer("430 119-8")
    .decoder("DHL100")
    .systemTyp(SystemTyp.SX1)
    .adresse(16)
    .build();

  public static final Fahrzeug lokE10_472 = Fahrzeug.builder()
    .betriebsnummer("E10 472")
    .systemTyp(SystemTyp.DCC)
    .adresse(1104)
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
    .funktion(new FahrzeugFunktion(FL, 0b0000_1100_0000_0000, 0b0000_1100_0000_0000, false, false, false, "Doppel-A"))
    .build();

  public static final Fahrzeug lokE50_047 = Fahrzeug.builder()
    .betriebsnummer("E50 047")
    .systemTyp(SystemTyp.SX2)
    .adresse(1050)
    .funktion(new FahrzeugFunktion(AF, 1, false, false, false, "Führerstandsbeleuchtung"))
    .funktion(new FahrzeugFunktion(FG, 2, false, false, false, "Motor"))
    .funktion(new FahrzeugFunktion(BG, 3, true, false, false, "Pfeife lang"))
    .funktion(new FahrzeugFunktion(AF, 4, false, false, false, "Direktsteuerung"))
    .funktion(new FahrzeugFunktion(BG, 5, true, true, false, "Pfeife kurz"))
    .funktion(new FahrzeugFunktion(BA, 7, true, false, false, "Schaffnerpfiff"))
    .funktion(new FahrzeugFunktion(BG, 9, false, false, false, "Bremsenquietschen aus"))
    .funktion(new FahrzeugFunktion(BG, 10, true, false, false, "Ankuppeln"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000, false, false, false, "beidseitig"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000, false, false, false, "nur Seite 1"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000, false, false, false, "nur Seite 2"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000, false, false, false, "Doppel-A"))
    .build();

  public static final Fahrzeug lokE9103 = Fahrzeug.builder()
    .betriebsnummer("E9103")
    .decoder("DHL100")
    .systemTyp(SystemTyp.SX1)
    .adresse(28)
    .build();

  public static final Fahrzeug lokV100_1365 = Fahrzeug.builder()
    .betriebsnummer("V100 1365")
    .systemTyp(SystemTyp.SX2)
    .adresse(1365)
    .funktion(new FahrzeugFunktion(FG, 2, false, false, false, "Motor"))
    .funktion(new FahrzeugFunktion(BG, 3, true, true, false, "Signalhorn hoch"))
    .funktion(new FahrzeugFunktion(AF, 4, false, false, false, "Direktsteuerung"))
    .funktion(new FahrzeugFunktion(BG, 5, false, false, false, "Bremsenquietschen aus"))
    .funktion(new FahrzeugFunktion(AF, 7, false, false, false, "Führerstandsbeleuchtung"))
    .funktion(new FahrzeugFunktion(BG, 9, true, false, false, "Glocke"))
    .funktion(new FahrzeugFunktion(BG, 10, true, false, false, "Signalhorn tief"))
    .funktion(new FahrzeugFunktion(BG, 11, false, false, false, "Kompressor"))
    .funktion(new FahrzeugFunktion(BA, 12, true, false, false, "Stuttgart Hauptbahnhof, bitte alle aussteigen"))
    .funktion(new FahrzeugFunktion(BA, 13, true, false, false, "Schaffnerpfiff"))
    .funktion(new FahrzeugFunktion(BA, 14, true, false, false, "Türenschließen"))
    .funktion(new FahrzeugFunktion(AF, 15, false, false, true, "Geräusche ausblenden"))
    .funktion(new FahrzeugFunktion(BA, 16, true, false, false, "Bitte einsteigen ..."))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000, false, false, false, "beidseitig"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000, false, false, false, "nur Seite 1"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000, false, false, false, "nur Seite 2"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000, false, false, false, "Doppel-A"))
    .build();

  public static final Fahrzeug lokV200_116 = Fahrzeug.builder()
    .betriebsnummer("V200 116")
    .systemTyp(SystemTyp.DCC)
    .adresse(1200)
    .funktion(new FahrzeugFunktion(FG, 1, false, false, false, "Motor"))
    .funktion(new FahrzeugFunktion(BG, 2, true, true, false, "Signalhorn"))
    .funktion(new FahrzeugFunktion(FG, 3, false, false, false, "Motor 2"))
    .funktion(new FahrzeugFunktion(BG, 6, true, false, false, "Abschlammen"))
    .funktion(new FahrzeugFunktion(AF, 7, false, false, false, "Rangiergang"))
    .funktion(new FahrzeugFunktion(BG, 8, false, false, false, "Handbremse"))
    .funktion(new FahrzeugFunktion(BG, 9, false, false, false, "Kompressor"))
    .funktion(new FahrzeugFunktion(BG, 10, false, false, false, "Führerstandstür"))
    .funktion(new FahrzeugFunktion(BG, 11, false, false, false, "Maschinenraumtür"))
    .funktion(new FahrzeugFunktion(BG, 12, false, false, false, "Führerstandsfenster"))
    .funktion(new FahrzeugFunktion(BG, 13, false, false, false, "Seitenfenster"))
    .funktion(new FahrzeugFunktion(BG, 14, false, false, false, "Kraftstoffpumpe"))
    .funktion(new FahrzeugFunktion(BG, 15, false, false, false, "Speisepumpe"))
    .funktion(new FahrzeugFunktion(BG, 16, true, false, false, "Vorwärmgerät"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_0001_1000, 0b0000_0000_0000_0000, false, false, false, "beidseitig"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_0001_1000, 0b0000_0000_0000_1000, false, false, false, "nur weiß"))
    .funktion(new FahrzeugFunktion(FL, 0b0000_0000_0001_1000, 0b0000_0000_0001_0000, false, false, false, "nur rot"))
    .build();

  public static final Fahrzeug lok612_509_0 = Fahrzeug.builder()
    .betriebsnummer("612 509-0")
    .systemTyp(SystemTyp.DCC)
    .adresse(1612)
    .funktion(new FahrzeugFunktion(FG, 1, false, false, false, "Motor"))
    .funktion(new FahrzeugFunktion(AF, 2, false, false, false, "Innenbeleuchtung"))
    .funktion(new FahrzeugFunktion(BG, 3, true, true, false, "Signalhorn hoch"))
    .funktion(new FahrzeugFunktion(BG, 4, false, false, false, "Kompressor"))
    .funktion(new FahrzeugFunktion(BA, 5, true, false, false, "Schaffnerpfiff"))
    .funktion(new FahrzeugFunktion(BA, 7, true, false, false, "Bitte einsteigen ..., Türenschließen, Pfiff"))
    .funktion(new FahrzeugFunktion(BA, 8, true, false, false, "Bitte einsteigen ..."))
    .funktion(new FahrzeugFunktion(AF, 10, false, false, true, "Geräusche ausblenden"))
    .funktion(new FahrzeugFunktion(BG, 15, true, false, false, "Signalhorn tief kurz"))
    .funktion(new FahrzeugFunktion(BG, 16, true, false, false, "Signalhorn tief lang"))
    .build();

  public static final Fahrzeug lokVT_11_5019 = Fahrzeug.builder()
    .betriebsnummer("VT 11.5019")
    .decoder("DHL100")
    .systemTyp(SystemTyp.SX1)
    .adresse(1)
    .build();

  public static final Fahrzeug lokVT_98_9667 = Fahrzeug.builder()
    .betriebsnummer("VT 98 9667")
    .systemTyp(SystemTyp.DCC)
    .adresse(1098)
    .funktion(new FahrzeugFunktion(FG, 1, false, false, false, "Motor"))
    .funktion(new FahrzeugFunktion(BA, 2, true, false, false, "Türenschließen"))
    .funktion(new FahrzeugFunktion(BG, 3, true, true, false, "Signalhorn"))
    .funktion(new FahrzeugFunktion(BG, 5, true, false, false, "Glocke"))
    .funktion(new FahrzeugFunktion(AF, 6, false, false, true, "Geräusche ausblenden"))
    .funktion(new FahrzeugFunktion(AF, 7, false, false, false, "Innenbeleuchtung"))
    .build();

  // public static final Fahrzeug lokX_VT_98_9731 = new Fahrzeug("X VT 98.9731", "DHL100", SystemTyp.SX1, false, 6, 31);
  // public static final Fahrzeug lokX_103_118_6 = new Fahrzeug("X 103 118-6", "DHL100", SystemTyp.SX1, false, 13, 31);
  // public static final Fahrzeug lokX_110_222_7 = new Fahrzeug("X 110 222-7", "DHL100", SystemTyp.SX1, false, 17, 31,
  // new Fahrzeug.FahrzeugFunktion(1, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Horn", true, true));
  // public static final Fahrzeug lokX_111_205_1 = new Fahrzeug("X 111 205-1", "DHL100", SystemTyp.SX1, false, 15, 31);
  // public static final Fahrzeug lokX_120_002_1 = new Fahrzeug("X 120 002-1", "Tr66830", SystemTyp.SX1, false, 18, 31);
  // public static final Fahrzeug lokX_14283 = new Fahrzeug("X 14283", "DHL050", SystemTyp.SX1, false, 3, 31);
  // public static final Fahrzeug lok151_073_4 = new Fahrzeug("151 073-4", "DH10", SystemTyp.SX2, 1151);
  // public static final Fahrzeug lokX_212_216_6 = new Fahrzeug("X 212 216-6", "Tr66825", SystemTyp.SX1, false, 5, 31);
  // public static final Fahrzeug lokX_221_137_3 = new Fahrzeug("X 221 137-3", "Tr66835", SystemTyp.SX1, false, 19, 31);
  // public static final Fahrzeug lokX_614_083_4 = new Fahrzeug("X 614 083-4", "Tr66832", SystemTyp.SX1, false, 11, 31);
  // public static final Fahrzeug lokX_89_005 = new Fahrzeug("X 89 005", "DH05", SystemTyp.SX1, false, 7, 31);

  public static final Fahrzeug[] fahrzeuge = {
    lok103_003_0,
    lok110_389_4,
    lok112_491_6,
    lok151_032_0,
    lok194_183_0,
    lok217_001_7,
    lok217_014_0,
    lok230_001_0,
    //    lok323_673_4,
    lok332_262_5,
    lok430_119_8,
    lok612_509_0,
    lokE10_472,
    lokE50_047,
    lokE9103,
    lokV100_1365,
    lokV200_116,
    //    lokET_90_5019,
    lokVT_11_5019,
    lokVT_98_9667
  };

}

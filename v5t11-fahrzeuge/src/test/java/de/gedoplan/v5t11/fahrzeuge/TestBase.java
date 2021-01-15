package de.gedoplan.v5t11.fahrzeuge;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;

public class TestBase {

  @Inject
  FahrzeugRepository fahrzeugRepository;

  static boolean dbFilled = false;

  @BeforeEach
  void fillDb() {
    if (!dbFilled) {
      if (this.fahrzeugRepository.countAll() != fahrzeuge.length) {
        for (Fahrzeug f : fahrzeuge) {
          if (this.fahrzeugRepository.findById(f.getId()) == null)
          this.fahrzeugRepository.persist(f);
        }
      }
      dbFilled = true;
    }
  }

  public static final Fahrzeug lok103_003_0 = new Fahrzeug("103 003-0", null, SystemTyp.DCC, 1103,
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 1, false, false, false, "Maschinenraumbeleuchtung"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FG, 2, false, false, false, "Motor"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 3, true, true, false, "Pfeife"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 4, false, false, false, "Direktsteuerung"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 5, false, false, false, "Führerstandsbeleuchtung"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 7, true, false, false, "Schaffnerpfiff"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 9, false, false, false, "Bremsenquietschen aus"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 10, true, false, false, "Bitte einsteigen ..."),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 11, false, false, false, "Lüfter"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 12, true, false, false, "Sanden"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 13, true, false, false, "... an Gleis 1 fährt ein Zug durch ..."),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 14, true, false, false, "Betriebsgefahr ..."),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 15, false, false, false, "Lüfter2"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000, false, false, false, "beidseitig"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000, false, false, false, "nur Seite 1"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000, false, false, false, "nur Seite 2"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000, false, false, false, "Doppel-A"));

  public static final Fahrzeug lok110_389_4 = new Fahrzeug("110 389-4", null, SystemTyp.DCC, 1110,
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 1, true, false, false, "Pfeife lang"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FG, 2, false, false, false, "Motor"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 3, true, true, false, "Pfeife kurz"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 4, false, false, false, "Direktsteuerung"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 5, false, false, false, "Bremsenquietschen aus"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 7, true, false, false, "Schaffnerpfiff"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 9, true, false, false, "Türen schließen"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 10, true, false, false, "???"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 11, false, false, false, "Kompressor"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 12, true, false, false, "Sanden"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 13, true, false, false, "Kabinenfunk"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 14, true, false, false, "Lüfter"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 15, false, false, false, "Türenschließen"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 16, false, false, true, "Geräusche ausblenden"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000, false, false, false, "beidseitig"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000, false, false, false, "nur Seite 1"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000, false, false, false, "nur Seite 2"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000, false, false, false, "Doppel-A"));

  public static final Fahrzeug lok112_491_6 = new Fahrzeug("112 491-6", null, SystemTyp.DCC, 1112,
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FG, 1, false, false, false, "Motor"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 2, true, false, false, "Pfeife lang"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 3, true, false, false, "Schaffnerpfiff"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 4, true, false, false, "Kompressor"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 5, true, false, false, "Ankuppeln"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 6, false, false, false, "Direktsteuerung"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 7, true, false, false, "???"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 8, true, false, false, "Sanden"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 9, false, false, false, "Rangiergang"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 10, false, false, true, "Geräusche ausblenden"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 13, true, true, false, "Pfeife kurz"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 14, true, false, false, "Kurvenquietschen"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_1100_0000_0000, 0b0000_0000_0000_0000, false, false, false, "beidseitig"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_1100_0000_0000, 0b0000_0100_0000_0000, false, false, false, "nur Seite 1"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_1100_0000_0000, 0b0000_1000_0000_0000, false, false, false, "nur Seite 2"));

  public static final Fahrzeug lok151_032_0 = new Fahrzeug("151 032-0", "DHL100", SystemTyp.SX1, 20);

  public static final Fahrzeug lok194_183_0 = new Fahrzeug("194 183-0", "Zimo", SystemTyp.DCC, 1194,
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FG, 1, false, false, false, "Motor"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 2, true, true, false, "Pfeife kurz"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 3, true, false, false, "Pfeife lang"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 4, true, false, false, "Schaffnerpfiff"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 5, true, false, false, "Ankuppeln"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 6, false, false, false, "Direktsteuerung"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 7, false, false, false, "Kurvenquietschen"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 8, false, false, false, "Handpumpe"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 9, false, false, false, "Rangiergang"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 10, false, false, false, "Lüfter"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 11, false, false, false, "Kompressor"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 12, false, false, false, "Tür"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 14, false, false, true, "Geräusche ausblenden"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 15, true, false, false, "Sanden"));

  public static final Fahrzeug lok217_001_7 = new Fahrzeug("217 001-7", null, SystemTyp.SX2, 1217,
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 1, false, false, false, "Führerstandsbeleuchtung"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FG, 2, false, false, false, "Motor"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 3, true, true, false, "Signalhorn hoch"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 4, false, false, false, "Direktsteuerung"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 5, false, false, false, "Bremsenquietschen aus"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 7, true, false, false, "Signalhorn tief"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 9, true, false, false, "Hilfsdiesel"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 10, true, false, false, "Kompressor"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 11, true, false, false, "Schaffnerpfiff"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 12, true, false, false, "... an Gleis 1 fährt ein Zug durch ..."),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 13, true, false, false, "Sanden"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 14, true, false, false, "Bitte einsteigen ..."),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 15, false, false, true, "Geräusche ausblenden"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000, false, false, false, "beidseitig"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000, false, false, false, "nur Seite 1"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000, false, false, false, "nur Seite 2"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000, false, false, false, "Doppel-A"));

  public static final Fahrzeug lok217_014_0 = new Fahrzeug("217 014-0", "DH14B+Sound", SystemTyp.SX2, 2217,
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 1, false, false, false, "Führerstandsbeleuchtung"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FG, 2, false, false, false, "Motor"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 3, true, true, false, "Signalhorn hoch"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 4, false, false, false, "Direktsteuerung"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 5, false, false, false, "Bremsenquietschen aus"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 7, true, false, false, "Signalhorn tief"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 9, true, false, false, "Hilfsdiesel"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 10, true, false, false, "Kompressor"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 11, true, false, false, "Schaffnerpfiff"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000, false, false, false, "beidseitig"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000, false, false, false, "nur Seite 1"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000, false, false, false, "nur Seite 2"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000, false, false, false, "Doppel-A"));
  
  public static final Fahrzeug lok230_001_0 = new Fahrzeug("230 001-0", null, SystemTyp.DCC, 1230,
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 1, true, true, false, "Horn kurz"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FG, 2, false, false, false, "Motor"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 3, true, false, false, "Horn lang"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 4, false, false, false, "Direktsteuerung"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 5, true, false, false, "Druckluft ablassen"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 7, false, false, false, "Bremsenquietschen aus"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 9, true, false, false, "Sanden"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 10, true, false, false, "Schaffnerpfiff"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 11, true, false, false, "???"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 12, true, false, false, "Türen schließen"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000, false, false, false, "beidseitig"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000, false, false, false, "nur Seite 2"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000, false, false, false, "nur Seite 1"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000, false, false, false, "Doppel-A"));

  public static final Fahrzeug lok323_673_4 = new Fahrzeug("323 673-4", "DHL050", SystemTyp.SX1, 4);

  public static final Fahrzeug lok332_262_5 = new Fahrzeug("332 262-5", "DHL100", SystemTyp.SX1, 14);

  public static final Fahrzeug lok430_119_8 = new Fahrzeug("430 119-8", "DHL100", SystemTyp.SX1, 16);

  public static final Fahrzeug lokE10_472 = new Fahrzeug("E10 472", null, SystemTyp.DCC, 1104,
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FG, 1, false, false, false, "Motor"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 2, true, false, false, "Pfeife lang"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 3, true, false, false, "Schaffnerpfiff"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 4, true, false, false, "Kompressor"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 5, true, false, false, "Ankuppeln"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 6, false, false, false, "Direktsteuerung"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 7, true, false, false, "???"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 8, true, false, false, "Sanden"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 9, false, false, false, "Rangiergang"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 10, false, false, true, "Geräusche ausblenden"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 13, true, true, false, "Pfeife kurz"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 14, true, false, false, "Kurvenquietschen"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_1100_0000_0000, 0b0000_0000_0000_0000, false, false, false, "beidseitig"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_1100_0000_0000, 0b0000_0100_0000_0000, false, false, false, "nur Seite 1"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_1100_0000_0000, 0b0000_1000_0000_0000, false, false, false, "nur Seite 2"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_1100_0000_0000, 0b0000_1100_0000_0000, false, false, false, "Doppel-A"));

  public static final Fahrzeug lokE50_047 = new Fahrzeug("E50 047", null, SystemTyp.SX2, 1050,
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 1, false, false, false, "Führerstandsbeleuchtung"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FG, 2, false, false, false, "Motor"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 3, true, false, false, "Pfeife lang"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 4, false, false, false, "Direktsteuerung"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 5, true, true, false, "Pfeife kurz"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 7, true, false, false, "Schaffnerpfiff"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 9, false, false, false, "Bremsenquietschen aus"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 10, true, false, false, "Ankuppeln"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000, false, false, false, "beidseitig"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000, false, false, false, "nur Seite 1"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000, false, false, false, "nur Seite 2"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000, false, false, false, "Doppel-A"));

  public static final Fahrzeug lokE9103 = new Fahrzeug("E9103", "DHL100", SystemTyp.SX1, 28);

  public static final Fahrzeug lokV100_1365 = new Fahrzeug("V100 1365", null, SystemTyp.SX2, 1365,
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FG, 2, false, false, false, "Motor"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 3, true, true, false, "Signalhorn hoch"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 4, false, false, false, "Direktsteuerung"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 5, false, false, false, "Bremsenquietschen aus"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 7, false, false, false, "Führerstandsbeleuchtung"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 9, true, false, false, "Glocke"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 10, true, false, false, "Signalhorn tief"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 11, false, false, false, "Kompressor"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 12, true, false, false, "Stuttgart Hauptbahnhof, bitte alle aussteigen"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 13, true, false, false, "Schaffnerpfiff"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 14, true, false, false, "Türenschließen"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 15, false, false, true, "Geräusche ausblenden"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 16, true, false, false, "Bitte einsteigen ..."),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000, false, false, false, "beidseitig"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000, false, false, false, "nur Seite 1"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000, false, false, false, "nur Seite 2"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000, false, false, false, "Doppel-A"));

  public static final Fahrzeug lokV200_116 = new Fahrzeug("V200 116", null, SystemTyp.DCC, 1200,
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FG, 1, false, false, false, "Motor"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 2, true, true, false, "Signalhorn"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FG, 3, false, false, false, "Motor 2"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 6, true, false, false, "Abschlammen"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 7, false, false, false, "Rangiergang"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 8, false, false, false, "Handbremse"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 9, false, false, false, "Kompressor"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 10, false, false, false, "Führerstandstür"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 11, false, false, false, "Maschinenraumtür"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 12, false, false, false, "Führerstandsfenster"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 13, false, false, false, "Seitenfenster"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 14, false, false, false, "Kraftstoffpumpe"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 15, false, false, false, "Speisepumpe"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 16, true, false, false, "Vorwärmgerät"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_0001_1000, 0b0000_0000_0000_0000, false, false, false, "beidseitig"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_0001_1000, 0b0000_0000_0000_1000, false, false, false, "nur weiß"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, 0b0000_0000_0001_1000, 0b0000_0000_0001_0000, false, false, false, "nur rot"));

  public static final Fahrzeug lok612_509_0 = new Fahrzeug("612 509-0", null, SystemTyp.DCC, 1612,
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FG, 1, false, false, false, "Motor"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 2, false, false, false, "Innenbeleuchtung"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 3, true, true, false, "Signalhorn hoch"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 4, false, false, false, "Kompressor"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 5, true, false, false, "Schaffnerpfiff"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 7, true, false, false, "Bitte einsteigen ..., Türenschließen, Pfiff"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 8, true, false, false, "Bitte einsteigen ..."),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 10, false, false, true, "Geräusche ausblenden"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 15, true, false, false, "Signalhorn tief kurz"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 16, true, false, false, "Signalhorn tief lang"));

  public static final Fahrzeug lokET_90_5019 = new Fahrzeug("ET 90.5019", "DHL100", SystemTyp.SX1, 21);

  public static final Fahrzeug lokVT_11_5019 = new Fahrzeug("VT 11.5019", "DHL100", SystemTyp.SX1, 1);

  public static final Fahrzeug lokVT_98_9667 = new Fahrzeug("VT 98 9667", null, SystemTyp.DCC, 1098,
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FG, 1, false, false, false, "Motor"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, 2, true, false, false, "Türenschließen"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 3, true, true, false, "Signalhorn"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, 5, true, false, false, "Glocke"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 6, false, false, true, "Geräusche ausblenden"),
      new Fahrzeug.FahrzeugFunktion(Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, 7, false, false, false, "Innenbeleuchtung"));

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
      lok323_673_4,
      lok332_262_5,
      lok430_119_8,
      lok612_509_0,
      lokE10_472,
      lokE50_047,
      lokE9103,
      lokV100_1365,
      lokV200_116,
      lokET_90_5019,
      lokVT_11_5019,
      lokVT_98_9667
  };

}

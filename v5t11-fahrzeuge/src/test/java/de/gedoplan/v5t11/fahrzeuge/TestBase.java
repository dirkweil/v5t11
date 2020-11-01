package de.gedoplan.v5t11.fahrzeuge;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.util.domain.entity.SystemTyp;

import javax.inject.Inject;

import org.junit.jupiter.api.BeforeEach;

public class TestBase {

  @Inject
  FahrzeugRepository fahrzeugRepository;

  static boolean dbFilled = false;

  @BeforeEach
  void fillDb() {
    if (!dbFilled) {
      if (this.fahrzeugRepository.countAll() == 0) {
        for (Fahrzeug f : fahrzeuge) {
          this.fahrzeugRepository.persist(f);
        }
      }
      dbFilled = true;
    }
  }

  public static final Fahrzeug lok103_003_0 = new Fahrzeug("103 003-0", null, SystemTyp.DCC_L_126, 1103,
      new Fahrzeug.FahrzeugFunktion(1, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Maschinenraumbeleuchtung", false, false),
      new Fahrzeug.FahrzeugFunktion(2, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Motor", false, false),
      new Fahrzeug.FahrzeugFunktion(3, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Pfeife", true, true),
      new Fahrzeug.FahrzeugFunktion(4, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Direktsteuerung", false, false),
      new Fahrzeug.FahrzeugFunktion(5, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Führerstandsbeleuchtung", false, false),
      new Fahrzeug.FahrzeugFunktion(7, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "Schaffnerpfiff", true, false),
      new Fahrzeug.FahrzeugFunktion(9, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Bremsenquietschen aus", false, false),
      new Fahrzeug.FahrzeugFunktion(10, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "Bitte einsteigen ...", true, false),
      new Fahrzeug.FahrzeugFunktion(11, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Lüfter", false, false),
      new Fahrzeug.FahrzeugFunktion(12, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Sanden", true, false),
      new Fahrzeug.FahrzeugFunktion(13, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "... an Gleis 1 fährt ein Zug durch ...", true, false),
      new Fahrzeug.FahrzeugFunktion(14, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "Betriebsgefahr ...", true, false),
      new Fahrzeug.FahrzeugFunktion(15, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Lüfter2", false, false),
      new Fahrzeug.FahrzeugFunktion(30, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Beidseitig", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000),
      new Fahrzeug.FahrzeugFunktion(32, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Nur Seite 1", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000),
      new Fahrzeug.FahrzeugFunktion(31, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Nur Seite 2", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000),
      new Fahrzeug.FahrzeugFunktion(33, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Doppel-A", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000));
  public static final Fahrzeug lok110_389_4 = new Fahrzeug("110 389-4", null, SystemTyp.DCC_L_126, 1110,
      new Fahrzeug.FahrzeugFunktion(1, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Pfeife lang", true, false),
      new Fahrzeug.FahrzeugFunktion(2, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Motor", false, false),
      new Fahrzeug.FahrzeugFunktion(3, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Pfeife kurz", true, true),
      new Fahrzeug.FahrzeugFunktion(4, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Direktsteuerung", false, false),
      new Fahrzeug.FahrzeugFunktion(5, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Bremsenquietschen aus", false, false),
      new Fahrzeug.FahrzeugFunktion(7, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "Schaffnerpfiff", true, false),
      new Fahrzeug.FahrzeugFunktion(9, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "Türen schließen", true, false),
      new Fahrzeug.FahrzeugFunktion(10, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "???", true, false),
      new Fahrzeug.FahrzeugFunktion(11, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Kompressor", false, false),
      new Fahrzeug.FahrzeugFunktion(12, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Sanden", true, false),
      new Fahrzeug.FahrzeugFunktion(13, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Kabinenfunk", true, false),
      new Fahrzeug.FahrzeugFunktion(14, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Lüfter", true, false),
      new Fahrzeug.FahrzeugFunktion(15, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Türenschließen", false, false),
      new Fahrzeug.FahrzeugFunktion(16, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Geräusche ausblenden", false, false),
      new Fahrzeug.FahrzeugFunktion(30, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Beidseitig", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000),
      new Fahrzeug.FahrzeugFunktion(32, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Nur Seite 1", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000),
      new Fahrzeug.FahrzeugFunktion(31, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Nur Seite 2", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000),
      new Fahrzeug.FahrzeugFunktion(33, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Doppel-A", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000));
  public static final Fahrzeug lok112_491_6 = new Fahrzeug("112 491-6", null, SystemTyp.DCC_L_126, 1112,
      new Fahrzeug.FahrzeugFunktion(1, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Motor", false, false),
      new Fahrzeug.FahrzeugFunktion(2, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Pfeife lang", true, false),
      new Fahrzeug.FahrzeugFunktion(3, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "Schaffnerpfiff", true, false),
      new Fahrzeug.FahrzeugFunktion(4, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Kompressor", true, false),
      new Fahrzeug.FahrzeugFunktion(5, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Ankuppeln", true, false),
      new Fahrzeug.FahrzeugFunktion(6, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Direktsteuerung", false, false),
      new Fahrzeug.FahrzeugFunktion(7, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "???", true, false),
      new Fahrzeug.FahrzeugFunktion(8, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Sanden", true, false),
      new Fahrzeug.FahrzeugFunktion(9, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Rangiergang", false, false),
      new Fahrzeug.FahrzeugFunktion(10, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Geräusche ausblenden", false, false),
      new Fahrzeug.FahrzeugFunktion(13, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Pfeife kurz", true, true),
      new Fahrzeug.FahrzeugFunktion(14, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Kurvenquietschen", true, true),
      new Fahrzeug.FahrzeugFunktion(30, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Beidseitig", false, false, 0b0000_1100_0000_0000, 0b0000_0000_0000_0000),
      new Fahrzeug.FahrzeugFunktion(32, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Nur Seite 1", false, false, 0b0000_1100_0000_0000, 0b0000_0100_0000_0000),
      new Fahrzeug.FahrzeugFunktion(31, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Nur Seite 2", false, false, 0b0000_1100_0000_0000, 0b0000_1000_0000_0000));
  public static final Fahrzeug lok151_032_0 = new Fahrzeug("151 032-0", "DHL100", SystemTyp.SX1, 20);
  public static final Fahrzeug lok151_073_4 = new Fahrzeug("151 073-4", "DH10", SystemTyp.SX2, 1151);
  public static final Fahrzeug lok194_183_0 = new Fahrzeug("194 183-0", "Zimo", SystemTyp.DCC_L_126, 1194,
      new Fahrzeug.FahrzeugFunktion(1, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Motor", false, false),
      new Fahrzeug.FahrzeugFunktion(2, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Pfeife kurz", true, true),
      new Fahrzeug.FahrzeugFunktion(3, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Pfeife lang", true, false),
      new Fahrzeug.FahrzeugFunktion(4, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "Schaffnerpfiff", true, false),
      new Fahrzeug.FahrzeugFunktion(5, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Ankuppeln", true, false),
      new Fahrzeug.FahrzeugFunktion(6, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Direktsteuerung", false, false),
      new Fahrzeug.FahrzeugFunktion(7, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Kurvenquietschen", false, false),
      new Fahrzeug.FahrzeugFunktion(8, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Handpumpe", false, false),
      new Fahrzeug.FahrzeugFunktion(9, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Rangiergang", false, false),
      new Fahrzeug.FahrzeugFunktion(10, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Lüfter", false, false),
      new Fahrzeug.FahrzeugFunktion(11, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Kompressor", false, false),
      new Fahrzeug.FahrzeugFunktion(12, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Tür", false, false),
      new Fahrzeug.FahrzeugFunktion(14, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Geräusche ausblenden", false, false),
      new Fahrzeug.FahrzeugFunktion(15, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Sanden", true, false));
  public static final Fahrzeug lok210_004_8 = new Fahrzeug("210 004-8", "DHL100", SystemTyp.SX1, 2);
  public static final Fahrzeug lok217_001_7 = new Fahrzeug("217 001-7", null, SystemTyp.SX2, 1217,
      new Fahrzeug.FahrzeugFunktion(1, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Führerstandsbeleuchtung", false, false),
      new Fahrzeug.FahrzeugFunktion(2, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Motor", false, false),
      new Fahrzeug.FahrzeugFunktion(3, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Signalhorn hoch", true, true),
      new Fahrzeug.FahrzeugFunktion(4, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Direktsteuerung", false, false),
      new Fahrzeug.FahrzeugFunktion(5, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Bremsenquietschen aus", false, false),
      new Fahrzeug.FahrzeugFunktion(7, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Signalhorn hoch", true, false),
      new Fahrzeug.FahrzeugFunktion(9, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Hilfsdiesel", true, false),
      new Fahrzeug.FahrzeugFunktion(10, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Kompressor", true, false),
      new Fahrzeug.FahrzeugFunktion(11, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "Schaffnerpfiff", true, false),
      new Fahrzeug.FahrzeugFunktion(12, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "... an Gleis 1 fährt ein Zug durch ...", true, false),
      new Fahrzeug.FahrzeugFunktion(13, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Sanden", true, false),
      new Fahrzeug.FahrzeugFunktion(14, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "Bitte einsteigen ...", true, false),
      new Fahrzeug.FahrzeugFunktion(15, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Geräusche ein/ausblenden", true, false),
      new Fahrzeug.FahrzeugFunktion(30, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Beidseitig", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000),
      new Fahrzeug.FahrzeugFunktion(32, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Nur Seite 1", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000),
      new Fahrzeug.FahrzeugFunktion(31, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Nur Seite 2", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000),
      new Fahrzeug.FahrzeugFunktion(33, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Doppel-A", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000));
  public static final Fahrzeug lok230_001_0 = new Fahrzeug("230 001-0", null, SystemTyp.DCC_L_126, 1230,
      new Fahrzeug.FahrzeugFunktion(1, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Horn kurz", true, true),
      new Fahrzeug.FahrzeugFunktion(2, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Motor", false, false),
      new Fahrzeug.FahrzeugFunktion(3, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Horn lang", true, false),
      new Fahrzeug.FahrzeugFunktion(4, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Direktsteuerung", false, false),
      new Fahrzeug.FahrzeugFunktion(5, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Druckluft ablassen", true, false),
      new Fahrzeug.FahrzeugFunktion(7, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Bremsenquietschen aus", false, false),
      new Fahrzeug.FahrzeugFunktion(9, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Sanden", true, false),
      new Fahrzeug.FahrzeugFunktion(10, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "Schaffnerpfiff", true, false),
      new Fahrzeug.FahrzeugFunktion(11, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "???", true, false),
      new Fahrzeug.FahrzeugFunktion(12, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Türen schließen", true, false),
      new Fahrzeug.FahrzeugFunktion(30, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Beidseitig", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000),
      new Fahrzeug.FahrzeugFunktion(32, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Nur Seite 2", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000),
      new Fahrzeug.FahrzeugFunktion(31, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Nur Seite 1", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000),
      new Fahrzeug.FahrzeugFunktion(33, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Doppel-A", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000));
  public static final Fahrzeug lok430_119_8 = new Fahrzeug("430 119-8", "DHL100", SystemTyp.SX1, 16);
  public static final Fahrzeug lokE10_472 = new Fahrzeug("E10 472", null, SystemTyp.DCC_L_126, 1104,
      new Fahrzeug.FahrzeugFunktion(1, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Motor", false, false),
      new Fahrzeug.FahrzeugFunktion(2, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Pfeife lang", true, false),
      new Fahrzeug.FahrzeugFunktion(3, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "Schaffnerpfiff", true, false),
      new Fahrzeug.FahrzeugFunktion(4, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Kompressor", true, false),
      new Fahrzeug.FahrzeugFunktion(5, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Ankuppeln", true, false),
      new Fahrzeug.FahrzeugFunktion(6, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Direktsteuerung", false, false),
      new Fahrzeug.FahrzeugFunktion(7, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "???", true, false),
      new Fahrzeug.FahrzeugFunktion(8, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Sanden", true, false),
      new Fahrzeug.FahrzeugFunktion(9, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Rangiergang", false, false),
      new Fahrzeug.FahrzeugFunktion(10, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Geräusche ausblenden", false, false),
      new Fahrzeug.FahrzeugFunktion(13, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Pfeife kurz", true, true),
      new Fahrzeug.FahrzeugFunktion(14, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Kurvenquietschen", true, true),
      new Fahrzeug.FahrzeugFunktion(30, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Beidseitig", false, false, 0b0000_1100_0000_0000, 0b0000_0000_0000_0000),
      new Fahrzeug.FahrzeugFunktion(32, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Nur Seite 1", false, false, 0b0000_1100_0000_0000, 0b0000_0100_0000_0000),
      new Fahrzeug.FahrzeugFunktion(31, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Nur Seite 2", false, false, 0b0000_1100_0000_0000, 0b0000_1000_0000_0000),
      new Fahrzeug.FahrzeugFunktion(33, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Doppel-A", false, false, 0b0000_1100_0000_0000, 0b0000_1100_0000_0000));
  public static final Fahrzeug lokE50_047 = new Fahrzeug("E50 047", null, SystemTyp.SX2, 1050,
      new Fahrzeug.FahrzeugFunktion(1, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Führerstandsbeleuchtung", false, false),
      new Fahrzeug.FahrzeugFunktion(2, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Motor", false, false),
      new Fahrzeug.FahrzeugFunktion(3, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Pfeife lang", true, false),
      new Fahrzeug.FahrzeugFunktion(4, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Direktsteuerung", false, false),
      new Fahrzeug.FahrzeugFunktion(5, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Pfeife kurz", true, true),
      new Fahrzeug.FahrzeugFunktion(7, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "Schaffnerpfiff", true, false),
      new Fahrzeug.FahrzeugFunktion(9, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Bremsenquietschen aus", false, false),
      new Fahrzeug.FahrzeugFunktion(10, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Ankuppeln", true, false),
      new Fahrzeug.FahrzeugFunktion(30, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Beidseitig", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000),
      new Fahrzeug.FahrzeugFunktion(32, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Nur Seite 1", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000),
      new Fahrzeug.FahrzeugFunktion(31, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Nur Seite 2", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000),
      new Fahrzeug.FahrzeugFunktion(33, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Doppel-A", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000));
  public static final Fahrzeug lokE9103 = new Fahrzeug("E9103", "DHL100", SystemTyp.SX1, 28);
  public static final Fahrzeug lokV100_1365 = new Fahrzeug("V100 1365", null, SystemTyp.SX2, 1365,
      new Fahrzeug.FahrzeugFunktion(1, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Spitzensignal nur vorn", false, false),
      new Fahrzeug.FahrzeugFunktion(2, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Motor", false, false),
      new Fahrzeug.FahrzeugFunktion(3, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Signalhorn hoch", true, false),
      new Fahrzeug.FahrzeugFunktion(4, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Direktsteuerung", false, false),
      new Fahrzeug.FahrzeugFunktion(5, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Bremsenquietschen aus", false, false),
      new Fahrzeug.FahrzeugFunktion(7, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Führerstandsbeleuchtung", false, false),
      new Fahrzeug.FahrzeugFunktion(9, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Glocke", true, false),
      new Fahrzeug.FahrzeugFunktion(10, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Signalhorn tief", true, false),
      new Fahrzeug.FahrzeugFunktion(11, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Kompressor", false, false),
      new Fahrzeug.FahrzeugFunktion(12, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "Stuttgart Hauptbahnhof, bitte alle aussteigen", true, false),
      new Fahrzeug.FahrzeugFunktion(13, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "Schaffnerpfiff", true, false),
      new Fahrzeug.FahrzeugFunktion(14, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "Türenschließen", true, false),
      new Fahrzeug.FahrzeugFunktion(30, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Beidseitig", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000),
      new Fahrzeug.FahrzeugFunktion(32, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Nur Seite 1", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000),
      new Fahrzeug.FahrzeugFunktion(31, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Nur Seite 2", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000),
      new Fahrzeug.FahrzeugFunktion(33, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Doppel-A", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000));
  public static final Fahrzeug lokV200_116 = new Fahrzeug("V200 116", null, SystemTyp.DCC_L_126, 1200,
      new Fahrzeug.FahrzeugFunktion(1, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Motor", false, false),
      new Fahrzeug.FahrzeugFunktion(2, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Signalhorn", true, true),
      new Fahrzeug.FahrzeugFunktion(3, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Motor 2", false, false),
      new Fahrzeug.FahrzeugFunktion(6, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Abschlammen", true, false),
      new Fahrzeug.FahrzeugFunktion(7, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Rangiergang", false, false),
      new Fahrzeug.FahrzeugFunktion(8, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Handbremse", false, false),
      new Fahrzeug.FahrzeugFunktion(9, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Kompressor", false, false),
      new Fahrzeug.FahrzeugFunktion(10, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Führerstandstür", false, false),
      new Fahrzeug.FahrzeugFunktion(11, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Maschinenraumtür", false, false),
      new Fahrzeug.FahrzeugFunktion(12, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Führerstandsfenster", false, false),
      new Fahrzeug.FahrzeugFunktion(13, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Seitenfenster", false, false),
      new Fahrzeug.FahrzeugFunktion(14, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Kraftstoffpumpe", false, false),
      new Fahrzeug.FahrzeugFunktion(15, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Speisepumpe", false, false),
      new Fahrzeug.FahrzeugFunktion(16, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Vorwärmgerät", true, false),
      new Fahrzeug.FahrzeugFunktion(30, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Beidseitig", false, false, 0b0000_0000_0001_1000, 0b0000_0000_0000_0000),
      new Fahrzeug.FahrzeugFunktion(31, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Nur weiß", false, false, 0b0000_0000_0001_1000, 0b0000_0000_0000_1000),
      new Fahrzeug.FahrzeugFunktion(32, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.FL, "Nur rot", false, false, 0b0000_0000_0001_1000, 0b0000_0000_0001_0000));
  public static final Fahrzeug lok612_509_0 = new Fahrzeug("612 509-0", null, SystemTyp.DCC_L_126, 1612,
      new Fahrzeug.FahrzeugFunktion(1, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Motor", false, false),
      new Fahrzeug.FahrzeugFunktion(2, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Innenbeleuchtung", false, false),
      new Fahrzeug.FahrzeugFunktion(3, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Signalhorn hoch", true, true),
      new Fahrzeug.FahrzeugFunktion(4, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Kompressor", false, false),
      new Fahrzeug.FahrzeugFunktion(5, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "Schaffnerpfiff", true, false),
      new Fahrzeug.FahrzeugFunktion(7, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "Bitte einsteigen ..., Türenschließen, Pfiff", true, false),
      new Fahrzeug.FahrzeugFunktion(8, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "Bitte einsteigen ...", true, false),
      new Fahrzeug.FahrzeugFunktion(10, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Geräusche ausblenden", false, false),
      new Fahrzeug.FahrzeugFunktion(15, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Signalhorn tief kurz", true, false),
      new Fahrzeug.FahrzeugFunktion(16, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Signalhorn tief lang", true, false));
  public static final Fahrzeug lokET_90_5019 = new Fahrzeug("ET 90.5019", "DHL100", SystemTyp.SX1, 21);
  public static final Fahrzeug lokVT_11_5019 = new Fahrzeug("VT 11.5019", "DHL100", SystemTyp.SX1, 1);
  public static final Fahrzeug lokVT_98_9667 = new Fahrzeug("VT 98 9667", null, SystemTyp.DCC_L_126, 1098,
      new Fahrzeug.FahrzeugFunktion(1, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Motor", false, false),
      new Fahrzeug.FahrzeugFunktion(2, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BA, "Türenschließen", true, false),
      new Fahrzeug.FahrzeugFunktion(3, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Signalhorn", true, false),
      new Fahrzeug.FahrzeugFunktion(5, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.BG, "Glocke", true, false),
      new Fahrzeug.FahrzeugFunktion(6, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Geräusche ausblenden", false, false),
      new Fahrzeug.FahrzeugFunktion(7, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Innenbeleuchtung", false, false));

  // public static final Fahrzeug lokX_VT_98_9731 = new Fahrzeug("X VT 98.9731", "DHL100", SystemTyp.SX1, false, 6);
  // public static final Fahrzeug lokX_103_118_6 = new Fahrzeug("X 103 118-6", "DHL100", SystemTyp.SX1, false, 13);
  // public static final Fahrzeug lokX_110_222_7 = new Fahrzeug("X 110 222-7", "DHL100", SystemTyp.SX1, false, 17,
  // new Fahrzeug.FahrzeugFunktion(1, Fahrzeug.FahrzeugFunktion.FahrzeugFunktionsGruppe.AF, "Horn", true, true));
  // public static final Fahrzeug lokX_111_205_1 = new Fahrzeug("X 111 205-1", "DHL100", SystemTyp.SX1, false, 15);
  // public static final Fahrzeug lokX_120_002_1 = new Fahrzeug("X 120 002-1", "Tr66830", SystemTyp.SX1, false, 18);
  // public static final Fahrzeug lokX_14283 = new Fahrzeug("X 14283", "DHL050", SystemTyp.SX1, false, 3);
  // public static final Fahrzeug lokX_212_216_6 = new Fahrzeug("X 212 216-6", "Tr66825", SystemTyp.SX1, false, 5);
  // public static final Fahrzeug lokX_221_137_3 = new Fahrzeug("X 221 137-3", "Tr66835", SystemTyp.SX1, false, 19);
  // public static final Fahrzeug lokX_323_673_4 = new Fahrzeug("X 323 673-4", "DHL050", SystemTyp.SX1, false, 4);
  // public static final Fahrzeug lokX_332_262_5 = new Fahrzeug("X 332 262-5", "DHL100", SystemTyp.SX1, false, 14);
  // public static final Fahrzeug lokX_614_083_4 = new Fahrzeug("X 614 083-4", "Tr66832", SystemTyp.SX1, false, 11);
  // public static final Fahrzeug lokX_89_005 = new Fahrzeug("X 89 005", "DH05", SystemTyp.SX1, false, 7);

  public static final Fahrzeug[] fahrzeuge = {
      lok103_003_0,
      lok110_389_4,
      lok112_491_6,
      lok151_032_0,
      lok151_073_4,
      lok194_183_0,
      lok210_004_8,
      lok217_001_7,
      lok230_001_0,
      lok430_119_8,
      lok612_509_0,
      lokE10_472,
      lokE50_047,
      lokE9103,
      lokV100_1365,
      lokV200_116,
      lokET_90_5019,
      lokVT_11_5019,
      lokVT_98_9667,
      // lokX_103_118_6,
      // lokX_110_222_7,
      // lokX_111_205_1,
      // lokX_120_002_1,
      // lokX_14283,
      // lokX_151_032_0,
      // lokX_212_216_6,
      // lokX_221_137_3,
      // lokX_323_673_4,
      // lokX_332_262_5,
      // lokX_614_083_4,
      // lokX_89_005,
      // lokX_VT_98_9731,
  };

}

package de.gedoplan.v5t11.status.service.init;

import de.gedoplan.v5t11.status.entity.SystemTyp;
import de.gedoplan.v5t11.status.entity.lok.Lok;

public class TestLokData {

  public static final Lok lok103_003_0 = new Lok("103 003-0", null, SystemTyp.DCC, false, 1103, 126,
      new Lok.LokFunktion(1, Lok.LokFunktion.LokFunktionsGruppe.AF, "Maschinenraumbeleuchtung", false, false),
      new Lok.LokFunktion(2, Lok.LokFunktion.LokFunktionsGruppe.BG, "Motor", false, false),
      new Lok.LokFunktion(3, Lok.LokFunktion.LokFunktionsGruppe.BG, "Pfeife", true, true),
      new Lok.LokFunktion(4, Lok.LokFunktion.LokFunktionsGruppe.AF, "Direktsteuerung", false, false),
      new Lok.LokFunktion(5, Lok.LokFunktion.LokFunktionsGruppe.AF, "Führerstandsbeleuchtung", false, false),
      new Lok.LokFunktion(7, Lok.LokFunktion.LokFunktionsGruppe.BA, "Schaffnerpfiff", true, false),
      new Lok.LokFunktion(9, Lok.LokFunktion.LokFunktionsGruppe.BG, "Bremsenquietschen aus", false, false),
      new Lok.LokFunktion(10, Lok.LokFunktion.LokFunktionsGruppe.BA, "Bitte einsteigen ...", true, false),
      new Lok.LokFunktion(11, Lok.LokFunktion.LokFunktionsGruppe.BG, "Lüfter", false, false),
      new Lok.LokFunktion(12, Lok.LokFunktion.LokFunktionsGruppe.BG, "Sanden", true, false),
      new Lok.LokFunktion(13, Lok.LokFunktion.LokFunktionsGruppe.BA, "... an Gleis 1 fährt ein Zug durch ...", true, false),
      new Lok.LokFunktion(14, Lok.LokFunktion.LokFunktionsGruppe.BA, "Betriebsgefahr ...", true, false),
      new Lok.LokFunktion(15, Lok.LokFunktion.LokFunktionsGruppe.BG, "Lüfter2", false, false),
      new Lok.LokFunktion(30, Lok.LokFunktion.LokFunktionsGruppe.FL, "Beidseitig", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000),
      new Lok.LokFunktion(31, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 1", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000),
      new Lok.LokFunktion(32, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 2", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000),
      new Lok.LokFunktion(33, Lok.LokFunktion.LokFunktionsGruppe.FL, "Doppel-A", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000));
  public static final Lok lok103_118_6 = new Lok("103 118-6", "DHL100", SystemTyp.SX1, false, 13, 31);
  public static final Lok lok110_222_7 = new Lok("110 222-7", "DHL100", SystemTyp.SX1, false, 17, 31,
      new Lok.LokFunktion(1, Lok.LokFunktion.LokFunktionsGruppe.AF, "Horn", true, true));
  public static final Lok lok110_389_4 = new Lok("110 389-4", null, SystemTyp.DCC, false, 1110, 126,
      new Lok.LokFunktion(1, Lok.LokFunktion.LokFunktionsGruppe.BG, "Pfeife lang", true, false),
      new Lok.LokFunktion(2, Lok.LokFunktion.LokFunktionsGruppe.BG, "Motor", false, false),
      new Lok.LokFunktion(3, Lok.LokFunktion.LokFunktionsGruppe.BG, "Pfeife kurz", true, true),
      new Lok.LokFunktion(4, Lok.LokFunktion.LokFunktionsGruppe.AF, "Direktsteuerung", false, false),
      new Lok.LokFunktion(5, Lok.LokFunktion.LokFunktionsGruppe.BG, "Bremsenquietschen aus", false, false),
      new Lok.LokFunktion(7, Lok.LokFunktion.LokFunktionsGruppe.BA, "Schaffnerpfiff", true, false),
      new Lok.LokFunktion(9, Lok.LokFunktion.LokFunktionsGruppe.BA, "Türen schließen", true, false),
      new Lok.LokFunktion(10, Lok.LokFunktion.LokFunktionsGruppe.BA, "???", true, false),
      new Lok.LokFunktion(11, Lok.LokFunktion.LokFunktionsGruppe.BG, "Kompressor", false, false),
      new Lok.LokFunktion(12, Lok.LokFunktion.LokFunktionsGruppe.BG, "Sanden", true, false),
      new Lok.LokFunktion(13, Lok.LokFunktion.LokFunktionsGruppe.BG, "Kabinenfunk", true, false),
      new Lok.LokFunktion(14, Lok.LokFunktion.LokFunktionsGruppe.BG, "Lüfter", true, false),
      new Lok.LokFunktion(15, Lok.LokFunktion.LokFunktionsGruppe.BG, "Türenschließen", false, false),
      new Lok.LokFunktion(16, Lok.LokFunktion.LokFunktionsGruppe.AF, "Geräusche ausblenden", false, false),
      new Lok.LokFunktion(30, Lok.LokFunktion.LokFunktionsGruppe.FL, "Beidseitig", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000),
      new Lok.LokFunktion(31, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 1", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000),
      new Lok.LokFunktion(32, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 2", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000),
      new Lok.LokFunktion(33, Lok.LokFunktion.LokFunktionsGruppe.FL, "Doppel-A", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000));
  public static final Lok lok111_205_1 = new Lok("111 205-1", "DHL100", SystemTyp.SX1, false, 15, 31);
  public static final Lok lok120_002_1 = new Lok("120 002-1", "Tr66830", SystemTyp.SX1, false, 18, 31);
  public static final Lok lok14283 = new Lok("14283", "DHL050", SystemTyp.SX1, false, 3, 31);
  public static final Lok lok151_032_0 = new Lok("151 032-0", "DHL100", SystemTyp.SX1, false, 20, 31);
  public static final Lok lok151_073_4 = new Lok("151 073-4", "DH10", SystemTyp.SX1, false, 22, 31);
  public static final Lok lok210_004_8 = new Lok("210 004-8", "DHL100", SystemTyp.SX1, false, 2, 31);
  public static final Lok lok212_216_6 = new Lok("212 216-6", "Tr66825", SystemTyp.SX1, false, 5, 31);
  public static final Lok lok217_001_7 = new Lok("217 001-7", null, SystemTyp.SX2, false, 1217, 127,
      new Lok.LokFunktion(1, Lok.LokFunktion.LokFunktionsGruppe.AF, "Führerstandsbeleuchtung", false, false),
      new Lok.LokFunktion(2, Lok.LokFunktion.LokFunktionsGruppe.BG, "Motor", false, false),
      new Lok.LokFunktion(3, Lok.LokFunktion.LokFunktionsGruppe.BG, "Signalhorn hoch", true, true),
      new Lok.LokFunktion(4, Lok.LokFunktion.LokFunktionsGruppe.AF, "Direktsteuerung", false, false),
      new Lok.LokFunktion(5, Lok.LokFunktion.LokFunktionsGruppe.BG, "Bremsenquietschen aus", false, false),
      new Lok.LokFunktion(7, Lok.LokFunktion.LokFunktionsGruppe.BG, "Signalhorn hoch", true, false),
      new Lok.LokFunktion(9, Lok.LokFunktion.LokFunktionsGruppe.BG, "Hilfsdiesel", true, false),
      new Lok.LokFunktion(10, Lok.LokFunktion.LokFunktionsGruppe.BG, "Kompressor", true, false),
      new Lok.LokFunktion(11, Lok.LokFunktion.LokFunktionsGruppe.BA, "Schaffnerpfiff", true, false),
      new Lok.LokFunktion(12, Lok.LokFunktion.LokFunktionsGruppe.BA, "... an Gleis 1 fährt ein Zug durch ...", true, false),
      new Lok.LokFunktion(13, Lok.LokFunktion.LokFunktionsGruppe.BG, "Sanden", true, false),
      new Lok.LokFunktion(14, Lok.LokFunktion.LokFunktionsGruppe.BA, "Bitte einsteigen ...", true, false),
      new Lok.LokFunktion(15, Lok.LokFunktion.LokFunktionsGruppe.BG, "Geräusche ein/ausblenden", true, false),
      new Lok.LokFunktion(30, Lok.LokFunktion.LokFunktionsGruppe.FL, "Beidseitig", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000),
      new Lok.LokFunktion(31, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 1", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000),
      new Lok.LokFunktion(32, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 2", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000),
      new Lok.LokFunktion(33, Lok.LokFunktion.LokFunktionsGruppe.FL, "Doppel-A", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000));
  public static final Lok lok221_137_3 = new Lok("221 137-3", "Tr66835", SystemTyp.SX1, false, 19, 31);
  public static final Lok lok323_673_4 = new Lok("323 673-4", "DHL050", SystemTyp.SX1, false, 4, 31);
  public static final Lok lok332_262_5 = new Lok("332 262-5", "DHL100", SystemTyp.SX1, false, 14, 31);
  public static final Lok lok430_119_8 = new Lok("430 119-8", "DHL100", SystemTyp.SX1, false, 16, 31);
  public static final Lok lok614_083_4 = new Lok("614 083-4", "Tr66832", SystemTyp.SX1, false, 11, 31);
  public static final Lok lok89_005 = new Lok("89 005", "DH05", SystemTyp.SX1, false, 7, 31);
  public static final Lok lokE50_047 = new Lok("E50 047", null, SystemTyp.SX2, false, 1050, 127,
      new Lok.LokFunktion(1, Lok.LokFunktion.LokFunktionsGruppe.AF, "Führerstandsbeleuchtung", false, false),
      new Lok.LokFunktion(2, Lok.LokFunktion.LokFunktionsGruppe.BG, "Motor", false, false),
      new Lok.LokFunktion(3, Lok.LokFunktion.LokFunktionsGruppe.BG, "Pfeife lang", true, false),
      new Lok.LokFunktion(4, Lok.LokFunktion.LokFunktionsGruppe.AF, "Direktsteuerung", false, false),
      new Lok.LokFunktion(5, Lok.LokFunktion.LokFunktionsGruppe.BG, "Pfeife kurz", true, true),
      new Lok.LokFunktion(7, Lok.LokFunktion.LokFunktionsGruppe.BA, "Schaffnerpfiff", true, false),
      new Lok.LokFunktion(9, Lok.LokFunktion.LokFunktionsGruppe.BG, "Bremsenquietschen aus", false, false),
      new Lok.LokFunktion(10, Lok.LokFunktion.LokFunktionsGruppe.BG, "Ankuppeln", true, false),
      new Lok.LokFunktion(30, Lok.LokFunktion.LokFunktionsGruppe.FL, "Beidseitig", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000),
      new Lok.LokFunktion(31, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 1", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000),
      new Lok.LokFunktion(32, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 2", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000),
      new Lok.LokFunktion(33, Lok.LokFunktion.LokFunktionsGruppe.FL, "Doppel-A", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000));
  public static final Lok lokV100_1365 = new Lok("V100 1365", null, SystemTyp.SX2, false, 1365, 127,
      new Lok.LokFunktion(1, Lok.LokFunktion.LokFunktionsGruppe.FL, "Spitzensignal nur vorn", false, false),
      new Lok.LokFunktion(2, Lok.LokFunktion.LokFunktionsGruppe.BG, "Motor", false, false),
      new Lok.LokFunktion(3, Lok.LokFunktion.LokFunktionsGruppe.BG, "Signalhorn hoch", true, false),
      new Lok.LokFunktion(4, Lok.LokFunktion.LokFunktionsGruppe.AF, "Direktsteuerung", false, false),
      new Lok.LokFunktion(5, Lok.LokFunktion.LokFunktionsGruppe.BG, "Bremsenquietschen aus", false, false),
      new Lok.LokFunktion(7, Lok.LokFunktion.LokFunktionsGruppe.AF, "Führerstandsbeleuchtung", false, false),
      new Lok.LokFunktion(9, Lok.LokFunktion.LokFunktionsGruppe.BG, "Glocke", true, false),
      new Lok.LokFunktion(10, Lok.LokFunktion.LokFunktionsGruppe.BG, "Signalhorn tief", true, false),
      new Lok.LokFunktion(11, Lok.LokFunktion.LokFunktionsGruppe.BG, "Kompressor", false, false),
      new Lok.LokFunktion(12, Lok.LokFunktion.LokFunktionsGruppe.BA, "???", true, false),
      new Lok.LokFunktion(13, Lok.LokFunktion.LokFunktionsGruppe.BA, "Schaffnerpfiff", true, false),
      new Lok.LokFunktion(14, Lok.LokFunktion.LokFunktionsGruppe.BA, "Türenschließen", true, false),
      new Lok.LokFunktion(30, Lok.LokFunktion.LokFunktionsGruppe.FL, "Beidseitig", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000),
      new Lok.LokFunktion(31, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 1", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000),
      new Lok.LokFunktion(32, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 2", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000),
      new Lok.LokFunktion(33, Lok.LokFunktion.LokFunktionsGruppe.FL, "Doppel-A", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000));
  public static final Lok lok612_509_0 = new Lok("612 509-0", null, SystemTyp.DCC, false, 1612, 126,
      new Lok.LokFunktion(1, Lok.LokFunktion.LokFunktionsGruppe.BG, "Motor", false, false));
  public static final Lok lokET_90_5019 = new Lok("ET 90.5019", "DHL100", SystemTyp.SX1, false, 21, 31);
  public static final Lok lokVT_11_5019 = new Lok("VT 11.5019", "DHL100", SystemTyp.SX1, false, 1, 31);
  public static final Lok lokVT_98_9667 = new Lok("VT 98 9667", null, SystemTyp.DCC, false, 1098, 126,
      new Lok.LokFunktion(1, Lok.LokFunktion.LokFunktionsGruppe.BG, "Motor", false, false));
  public static final Lok lokVT_98_9731 = new Lok("VT 98.9731", "DHL100", SystemTyp.SX1, false, 6, 31);

  public static final Lok[] loks = {
      lok103_003_0,
      lok103_118_6,
      lok110_222_7,
      lok110_389_4,
      lok111_205_1,
      lok120_002_1,
      lok14283,
      lok151_032_0,
      lok151_073_4,
      lok212_216_6,
      lok210_004_8,
      lok217_001_7,
      lok221_137_3,
      lok323_673_4,
      lok332_262_5,
      lok430_119_8,
      lok612_509_0,
      lok614_083_4,
      lok89_005,
      lokE50_047,
      lokV100_1365,
      lokET_90_5019,
      lokVT_11_5019,
      lokVT_98_9667,
      lokVT_98_9731
  };
}

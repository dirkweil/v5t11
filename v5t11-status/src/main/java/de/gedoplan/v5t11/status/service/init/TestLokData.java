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
      new Lok.LokFunktion(32, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 1", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000),
      new Lok.LokFunktion(31, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 2", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000),
      new Lok.LokFunktion(33, Lok.LokFunktion.LokFunktionsGruppe.FL, "Doppel-A", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000));
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
      new Lok.LokFunktion(32, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 1", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000),
      new Lok.LokFunktion(31, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 2", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000),
      new Lok.LokFunktion(33, Lok.LokFunktion.LokFunktionsGruppe.FL, "Doppel-A", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000));
  public static final Lok lok112_491_6 = new Lok("112 491-6", null, SystemTyp.DCC, false, 1112, 126,
      new Lok.LokFunktion(1, Lok.LokFunktion.LokFunktionsGruppe.BG, "Motor", false, false),
      new Lok.LokFunktion(2, Lok.LokFunktion.LokFunktionsGruppe.BG, "Pfeife lang", true, false),
      new Lok.LokFunktion(3, Lok.LokFunktion.LokFunktionsGruppe.BA, "Schaffnerpfiff", true, false),
      new Lok.LokFunktion(4, Lok.LokFunktion.LokFunktionsGruppe.BG, "Kompressor", true, false),
      new Lok.LokFunktion(5, Lok.LokFunktion.LokFunktionsGruppe.BG, "Ankuppeln", true, false),
      new Lok.LokFunktion(6, Lok.LokFunktion.LokFunktionsGruppe.AF, "Direktsteuerung", false, false),
      new Lok.LokFunktion(7, Lok.LokFunktion.LokFunktionsGruppe.BA, "???", true, false),
      new Lok.LokFunktion(8, Lok.LokFunktion.LokFunktionsGruppe.BG, "Sanden", true, false),
      new Lok.LokFunktion(9, Lok.LokFunktion.LokFunktionsGruppe.AF, "Rangiergang", false, false),
      new Lok.LokFunktion(10, Lok.LokFunktion.LokFunktionsGruppe.AF, "Geräusche ausblenden", false, false),
      new Lok.LokFunktion(13, Lok.LokFunktion.LokFunktionsGruppe.BG, "Pfeife kurz", true, true),
      new Lok.LokFunktion(14, Lok.LokFunktion.LokFunktionsGruppe.BG, "Kurvenquietschen", true, true),
      new Lok.LokFunktion(30, Lok.LokFunktion.LokFunktionsGruppe.FL, "Beidseitig", false, false, 0b0000_1100_0000_0000, 0b0000_0000_0000_0000),
      new Lok.LokFunktion(32, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 1", false, false, 0b0000_1100_0000_0000, 0b0000_0100_0000_0000),
      new Lok.LokFunktion(31, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 2", false, false, 0b0000_1100_0000_0000, 0b0000_1000_0000_0000));
  public static final Lok lok151_073_4 = new Lok("151 073-4", "DH10", SystemTyp.SX2, false, 1151, 127);
  public static final Lok lok210_004_8 = new Lok("210 004-8", "DHL100", SystemTyp.SX1, false, 2, 31);
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
      new Lok.LokFunktion(32, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 1", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000),
      new Lok.LokFunktion(31, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 2", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000),
      new Lok.LokFunktion(33, Lok.LokFunktion.LokFunktionsGruppe.FL, "Doppel-A", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000));
  public static final Lok lok230_001_0 = new Lok("230 001-0", null, SystemTyp.DCC, false, 1230, 126,
      new Lok.LokFunktion(1, Lok.LokFunktion.LokFunktionsGruppe.BG, "Horn kurz", true, true),
      new Lok.LokFunktion(2, Lok.LokFunktion.LokFunktionsGruppe.BG, "Motor", false, false),
      new Lok.LokFunktion(3, Lok.LokFunktion.LokFunktionsGruppe.BG, "Horn lang", true, false),
      new Lok.LokFunktion(4, Lok.LokFunktion.LokFunktionsGruppe.AF, "Direktsteuerung", false, false),
      new Lok.LokFunktion(5, Lok.LokFunktion.LokFunktionsGruppe.BG, "Druckluft ablassen", true, false),
      new Lok.LokFunktion(7, Lok.LokFunktion.LokFunktionsGruppe.BG, "Bremsenquietschen aus", false, false),
      new Lok.LokFunktion(9, Lok.LokFunktion.LokFunktionsGruppe.BG, "Sanden", true, false),
      new Lok.LokFunktion(10, Lok.LokFunktion.LokFunktionsGruppe.BA, "Schaffnerpfiff", true, false),
      new Lok.LokFunktion(11, Lok.LokFunktion.LokFunktionsGruppe.BA, "???", true, false),
      new Lok.LokFunktion(12, Lok.LokFunktion.LokFunktionsGruppe.BG, "Türen schließen", true, false),
      new Lok.LokFunktion(30, Lok.LokFunktion.LokFunktionsGruppe.FL, "Beidseitig", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0000_0000),
      new Lok.LokFunktion(32, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 2", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000),
      new Lok.LokFunktion(31, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 1", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000),
      new Lok.LokFunktion(33, Lok.LokFunktion.LokFunktionsGruppe.FL, "Doppel-A", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000));
  public static final Lok lok430_119_8 = new Lok("430 119-8", "DHL100", SystemTyp.SX1, false, 16, 31);
  public static final Lok lokE9103 = new Lok("E9103", "DHL100", SystemTyp.SX1, false, 28, 31);
  public static final Lok lokE10_472 = new Lok("E10 472", null, SystemTyp.DCC, false, 1104, 126,
      new Lok.LokFunktion(1, Lok.LokFunktion.LokFunktionsGruppe.BG, "Motor", false, false),
      new Lok.LokFunktion(2, Lok.LokFunktion.LokFunktionsGruppe.BG, "Pfeife lang", true, false),
      new Lok.LokFunktion(3, Lok.LokFunktion.LokFunktionsGruppe.BA, "Schaffnerpfiff", true, false),
      new Lok.LokFunktion(4, Lok.LokFunktion.LokFunktionsGruppe.BG, "Kompressor", true, false),
      new Lok.LokFunktion(5, Lok.LokFunktion.LokFunktionsGruppe.BG, "Ankuppeln", true, false),
      new Lok.LokFunktion(6, Lok.LokFunktion.LokFunktionsGruppe.AF, "Direktsteuerung", false, false),
      new Lok.LokFunktion(7, Lok.LokFunktion.LokFunktionsGruppe.BA, "???", true, false),
      new Lok.LokFunktion(8, Lok.LokFunktion.LokFunktionsGruppe.BG, "Sanden", true, false),
      new Lok.LokFunktion(9, Lok.LokFunktion.LokFunktionsGruppe.AF, "Rangiergang", false, false),
      new Lok.LokFunktion(10, Lok.LokFunktion.LokFunktionsGruppe.AF, "Geräusche ausblenden", false, false),
      new Lok.LokFunktion(13, Lok.LokFunktion.LokFunktionsGruppe.BG, "Pfeife kurz", true, true),
      new Lok.LokFunktion(14, Lok.LokFunktion.LokFunktionsGruppe.BG, "Kurvenquietschen", true, true),
      new Lok.LokFunktion(30, Lok.LokFunktion.LokFunktionsGruppe.FL, "Beidseitig", false, false, 0b0000_1100_0000_0000, 0b0000_0000_0000_0000),
      new Lok.LokFunktion(32, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 1", false, false, 0b0000_1100_0000_0000, 0b0000_0100_0000_0000),
      new Lok.LokFunktion(31, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 2", false, false, 0b0000_1100_0000_0000, 0b0000_1000_0000_0000),
      new Lok.LokFunktion(33, Lok.LokFunktion.LokFunktionsGruppe.FL, "Doppel-A", false, false, 0b0000_1100_0000_0000, 0b0000_1100_0000_0000));
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
      new Lok.LokFunktion(32, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 1", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000),
      new Lok.LokFunktion(31, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 2", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000),
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
      new Lok.LokFunktion(32, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 1", false, false, 0b0000_0000_1010_0000, 0b0000_0000_0010_0000),
      new Lok.LokFunktion(31, Lok.LokFunktion.LokFunktionsGruppe.FL, "Nur Seite 2", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1000_0000),
      new Lok.LokFunktion(33, Lok.LokFunktion.LokFunktionsGruppe.FL, "Doppel-A", false, false, 0b0000_0000_1010_0000, 0b0000_0000_1010_0000));
  public static final Lok lok612_509_0 = new Lok("612 509-0", null, SystemTyp.DCC, false, 1612, 126,
      new Lok.LokFunktion(1, Lok.LokFunktion.LokFunktionsGruppe.BG, "Motor", false, false));
  public static final Lok lokET_90_5019 = new Lok("ET 90.5019", "DHL100", SystemTyp.SX1, false, 21, 31);
  public static final Lok lokVT_11_5019 = new Lok("VT 11.5019", "DHL100", SystemTyp.SX1, false, 1, 31);
  public static final Lok lokVT_98_9667 = new Lok("VT 98 9667", null, SystemTyp.DCC, false, 1098, 126,
      new Lok.LokFunktion(1, Lok.LokFunktion.LokFunktionsGruppe.BG, "Motor", false, false));

  // public static final Lok lokX_VT_98_9731 = new Lok("X VT 98.9731", "DHL100", SystemTyp.SX1, false, 6, 31);
  // public static final Lok lokX_103_118_6 = new Lok("X 103 118-6", "DHL100", SystemTyp.SX1, false, 13, 31);
  // public static final Lok lokX_110_222_7 = new Lok("X 110 222-7", "DHL100", SystemTyp.SX1, false, 17, 31,
  // new Lok.LokFunktion(1, Lok.LokFunktion.LokFunktionsGruppe.AF, "Horn", true, true));
  // public static final Lok lokX_111_205_1 = new Lok("X 111 205-1", "DHL100", SystemTyp.SX1, false, 15, 31);
  // public static final Lok lokX_120_002_1 = new Lok("X 120 002-1", "Tr66830", SystemTyp.SX1, false, 18, 31);
  // public static final Lok lokX_14283 = new Lok("X 14283", "DHL050", SystemTyp.SX1, false, 3, 31);
  // public static final Lok lokX_151_032_0 = new Lok("X 151 032-0", "DHL100", SystemTyp.SX1, false, 20, 31);
  // public static final Lok lokX_212_216_6 = new Lok("X 212 216-6", "Tr66825", SystemTyp.SX1, false, 5, 31);
  // public static final Lok lokX_221_137_3 = new Lok("X 221 137-3", "Tr66835", SystemTyp.SX1, false, 19, 31);
  // public static final Lok lokX_323_673_4 = new Lok("X 323 673-4", "DHL050", SystemTyp.SX1, false, 4, 31);
  // public static final Lok lokX_332_262_5 = new Lok("X 332 262-5", "DHL100", SystemTyp.SX1, false, 14, 31);
  // public static final Lok lokX_614_083_4 = new Lok("X 614 083-4", "Tr66832", SystemTyp.SX1, false, 11, 31);
  // public static final Lok lokX_89_005 = new Lok("X 89 005", "DH05", SystemTyp.SX1, false, 7, 31);

  public static final Lok[] loks = {
      lok103_003_0,
      lok110_389_4,
      lok112_491_6,
      lok151_073_4,
      lok210_004_8,
      lok217_001_7,
      lok230_001_0,
      lok430_119_8,
      lok612_509_0,
      lokE10_472,
      lokE50_047,
      lokE9103,
      lokV100_1365,
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

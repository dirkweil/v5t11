package de.gedoplan.v5t11.status.service.init;

import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.status.entity.fahrzeug.FahrzeugId;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;

public class TestLokData {

  public static final Fahrzeug lok103_003_0 = new Fahrzeug(new FahrzeugId(SystemTyp.DCC, 1103));
  public static final Fahrzeug lok110_389_4 = new Fahrzeug(new FahrzeugId(SystemTyp.DCC, 1110));
  public static final Fahrzeug lok112_491_6 = new Fahrzeug(new FahrzeugId(SystemTyp.DCC, 1112));
  public static final Fahrzeug lok151_032_0 = new Fahrzeug(new FahrzeugId(SystemTyp.SX1, 20));
  public static final Fahrzeug lok194_183_0 = new Fahrzeug(new FahrzeugId(SystemTyp.DCC, 1194));
  public static final Fahrzeug lok210_004_8 = new Fahrzeug(new FahrzeugId(SystemTyp.SX1, 2));
  public static final Fahrzeug lok217_001_7 = new Fahrzeug(new FahrzeugId(SystemTyp.SX2, 1217));
  public static final Fahrzeug lok230_001_0 = new Fahrzeug(new FahrzeugId(SystemTyp.DCC, 1230));
  public static final Fahrzeug lok323_673_4 = new Fahrzeug(new FahrzeugId(SystemTyp.SX1, 4));
  public static final Fahrzeug lok332_262_5 = new Fahrzeug(new FahrzeugId(SystemTyp.SX1, 14));
  public static final Fahrzeug lok430_119_8 = new Fahrzeug(new FahrzeugId(SystemTyp.SX1, 16));
  public static final Fahrzeug lokE10_472 = new Fahrzeug(new FahrzeugId(SystemTyp.DCC, 1104));
  public static final Fahrzeug lokE50_047 = new Fahrzeug(new FahrzeugId(SystemTyp.SX2, 1050));
  public static final Fahrzeug lokE9103 = new Fahrzeug(new FahrzeugId(SystemTyp.SX1, 28));
  public static final Fahrzeug lokV100_1365 = new Fahrzeug(new FahrzeugId(SystemTyp.SX2, 1365));
  public static final Fahrzeug lokV200_116 = new Fahrzeug(new FahrzeugId(SystemTyp.DCC, 1200));
  public static final Fahrzeug lok612_509_0 = new Fahrzeug(new FahrzeugId(SystemTyp.DCC, 1612));
  public static final Fahrzeug lokET_90_5019 = new Fahrzeug(new FahrzeugId(SystemTyp.SX1, 21));
  public static final Fahrzeug lokVT_11_5019 = new Fahrzeug(new FahrzeugId(SystemTyp.SX1, 1));
  public static final Fahrzeug lokVT_98_9667 = new Fahrzeug(new FahrzeugId(SystemTyp.DCC, 1098));

  public static final Fahrzeug[] loks = {
      lok103_003_0,
      lok110_389_4,
      lok112_491_6,
      lok151_032_0,
      lok194_183_0,
      lok210_004_8,
      lok217_001_7,
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

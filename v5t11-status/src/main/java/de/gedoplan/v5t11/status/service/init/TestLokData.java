package de.gedoplan.v5t11.status.service.init;

import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;

public class TestLokData {

  public static final Fahrzeug lok103_003_0 = createFahrzeug(SystemTyp.DCC, 1103, 0x0004);
  public static final Fahrzeug lok110_389_4 = createFahrzeug(SystemTyp.DCC, 1110, 0x0004);
  public static final Fahrzeug lok112_491_6 = createFahrzeug(SystemTyp.DCC, 1112, 0x1000);
  public static final Fahrzeug lok151_032_0 = createFahrzeug(SystemTyp.SX1, 20, 0);
  public static final Fahrzeug lok194_183_0 = createFahrzeug(SystemTyp.DCC, 1194, 0x0002);
  public static final Fahrzeug lok210_004_8 = createFahrzeug(SystemTyp.SX1, 2, 0);
  public static final Fahrzeug lok217_001_7 = createFahrzeug(SystemTyp.SX2, 1217, 0x0004);
  public static final Fahrzeug lok230_001_0 = createFahrzeug(SystemTyp.DCC, 1230, 0x0001);
  public static final Fahrzeug lok323_673_4 = createFahrzeug(SystemTyp.SX1, 4, 0);
  public static final Fahrzeug lok332_262_5 = createFahrzeug(SystemTyp.SX1, 14, 0);
  public static final Fahrzeug lok430_119_8 = createFahrzeug(SystemTyp.SX1, 16, 0);
  public static final Fahrzeug lokE10_472 = createFahrzeug(SystemTyp.DCC, 1104, 0x1000);
  public static final Fahrzeug lokE50_047 = createFahrzeug(SystemTyp.SX2, 1050, 0x0010);
  public static final Fahrzeug lokE9103 = createFahrzeug(SystemTyp.SX1, 28, 0);
  public static final Fahrzeug lokV100_1365 = createFahrzeug(SystemTyp.SX2, 1365, 0x0004);
  public static final Fahrzeug lokV200_116 = createFahrzeug(SystemTyp.DCC, 1200, 0x0002);
  public static final Fahrzeug lok612_509_0 = createFahrzeug(SystemTyp.DCC, 1612, 0x0004);
  public static final Fahrzeug lokET_90_5019 = createFahrzeug(SystemTyp.SX1, 21, 0);
  public static final Fahrzeug lokVT_11_5019 = createFahrzeug(SystemTyp.SX1, 1, 0);
  public static final Fahrzeug lokVT_98_9667 = createFahrzeug(SystemTyp.DCC, 1098, 0x0004);

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

  private static Fahrzeug createFahrzeug(SystemTyp systemTyp, int adresse, int hornBits) {
    Fahrzeug fahrzeug = new Fahrzeug(new FahrzeugId(systemTyp, adresse));
    // fahrzeug.setHornBits(hornBits);
    return fahrzeug;
  }
}

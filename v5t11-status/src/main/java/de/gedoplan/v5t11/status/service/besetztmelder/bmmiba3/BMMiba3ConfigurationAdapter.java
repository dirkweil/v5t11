package de.gedoplan.v5t11.status.service.besetztmelder.bmmiba3;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.service.ConfigurationAdapter;

import lombok.Getter;

/**
 * Konfigurations-Adapter für Besetztmelder des Typs BM-MIBA 3.
 *
 * @author dw
 */
@Getter
public class BMMiba3ConfigurationAdapter extends ConfigurationAdapter {
  private ConfigurationPropertyAdapter<Integer> ansprechVerzoegerung;
  private ConfigurationPropertyAdapter<Integer> abfallVerzoegerung;
  private ConfigurationPropertyAdapter<Zeittakt> zeittakt;
  private ConfigurationPropertyAdapter<Boolean> meldungsNegation;
  private ConfigurationPropertyAdapter<MeldungsModus> meldungBeiZeStopp;
  private ConfigurationPropertyAdapter<MeldungsModus> meldungBeiFehlendemFahrstrom;
  private ConfigurationPropertyAdapter<MeldungsSpeicherung> meldungsSpeicherung;

  public BMMiba3ConfigurationAdapter(BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    super(istConfiguration, sollConfiguration);

    this.ansprechVerzoegerung = new ConfigurationPropertyAdapter<>(this.istProperties, "ansprechVerzoegerung", 8, this.sollProperties, Integer.class);
    this.abfallVerzoegerung = new ConfigurationPropertyAdapter<>(this.istProperties, "abfallVerzoegerung", 100, this.sollProperties, Integer.class);
    this.zeittakt = new ConfigurationPropertyAdapter<>(this.istProperties, "zeitTakt", Zeittakt.TAKT_10MS, this.sollProperties, Zeittakt.class);
    this.meldungsNegation = new ConfigurationPropertyAdapter<>(this.istProperties, "meldungsNegation", false, this.sollProperties, Boolean.class);
    this.meldungBeiZeStopp = new ConfigurationPropertyAdapter<>(this.istProperties, "meldungBeiZeStopp", MeldungsModus.UNVERAENDERT, this.sollProperties, MeldungsModus.class);
    this.meldungBeiFehlendemFahrstrom = new ConfigurationPropertyAdapter<>(this.istProperties, "meldungBeiFehlendemFahrstrom", MeldungsModus.UNVERAENDERT, this.sollProperties, MeldungsModus.class);
    this.meldungsSpeicherung = new ConfigurationPropertyAdapter<>(this.istProperties, "meldungsSpeicherung", MeldungsSpeicherung.ZE_STOPP, this.sollProperties, MeldungsSpeicherung.class);
  }

  // public static BMMiba3ConfigurationAdapter createInstance(Baustein baustein, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
  // return new BMMiba3ConfigurationAdapter(istConfiguration, sollConfiguration);
  // }

  public static enum MeldungsModus {
    STANDARD {
      @Override
      public int getBits() {
        return 0b00;
      }
    },
    UNVERAENDERT {
      @Override
      public int getBits() {
        return 0b10;
      }
    },
    GLEISFREI {
      @Override
      public int getBits() {
        return 0b01;
      }
    },
    GLEISBELEGT {
      @Override
      public int getBits() {
        return 0b11;
      }
    };

    public static MeldungsModus valueOf(int bits) {
      for (MeldungsModus meldungsModus : MeldungsModus.values()) {
        if (meldungsModus.getBits() == bits) {
          return meldungsModus;
        }
      }
      throw new IllegalArgumentException("Ungueltige Meldungsmodus-Bits");
    }

    public abstract int getBits();
  }

  public static enum Zeittakt {
    TAKT_10MS {
      @Override
      public int getBits() {
        return 0b00;
      }
    },
    TAKT_20MS {
      @Override
      public int getBits() {
        return 0b10;
      }
    },
    TAKT_40MS {
      @Override
      public int getBits() {
        return 0b01;
      }
    },
    TAKT_80MS {
      @Override
      public int getBits() {
        return 0b11;
      }
    };

    public static Zeittakt valueof(int bits) {
      for (Zeittakt zeittakt : Zeittakt.values()) {
        if (zeittakt.getBits() == bits) {
          return zeittakt;
        }
      }
      throw new IllegalArgumentException("Ungueltige Zeittakt-Bits");
    }

    public abstract int getBits();
  }

  public static enum MeldungsSpeicherung {
    IMMER {
      @Override
      public int getBits() {
        return 0b0;
      }
    },
    ZE_STOPP {
      @Override
      public int getBits() {
        return 0b1;
      }
    };

    public static MeldungsSpeicherung valueof(int bits) {
      for (MeldungsSpeicherung meldungsSpeicherung : MeldungsSpeicherung.values()) {
        if (meldungsSpeicherung.getBits() == bits) {
          return meldungsSpeicherung;
        }
      }
      throw new IllegalArgumentException("Ungueltige MeldungsSpeicherung-Bits");
    }

    public abstract int getBits();
  }

}

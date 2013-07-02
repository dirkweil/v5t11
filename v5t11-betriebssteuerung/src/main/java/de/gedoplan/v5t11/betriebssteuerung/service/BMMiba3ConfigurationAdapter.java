package de.gedoplan.v5t11.betriebssteuerung.service;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;

import java.util.Collections;
import java.util.Map;

public class BMMiba3ConfigurationAdapter extends ConfigurationAdapter
{
  private ConfigurationPropertyAdapter<Integer>             ansprechVerzoegerung;
  private ConfigurationPropertyAdapter<Integer>             abfallVerzoegerung;
  private ConfigurationPropertyAdapter<Zeittakt>            zeittakt;
  private ConfigurationPropertyAdapter<Boolean>             meldungsNegation;
  private ConfigurationPropertyAdapter<MeldungsModus>       meldungBeiZeStopp;
  private ConfigurationPropertyAdapter<MeldungsModus>       meldungBeiFehlendemFahrstrom;
  private ConfigurationPropertyAdapter<MeldungsSpeicherung> meldungsSpeicherung;

  /**
   * @param istConfiguration
   * @param sollConfiguration
   */
  public BMMiba3ConfigurationAdapter(BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration)
  {
    super(istConfiguration, sollConfiguration);

    Map<String, String> istProperties = this.istConfiguration.getProperties();
    Map<String, String> sollProperties = null;
    if (this.sollConfiguration != null)
    {
      sollProperties = this.sollConfiguration.getProperties();
    }
    if (sollProperties == null)
    {
      sollProperties = Collections.emptyMap();
    }

    this.ansprechVerzoegerung = new ConfigurationPropertyAdapter<>(istProperties, "ansprechVerzoegerung", 8, sollProperties, Integer.class);
    this.abfallVerzoegerung = new ConfigurationPropertyAdapter<>(istProperties, "abfallVerzoegerung", 50, sollProperties, Integer.class);
    this.zeittakt = new ConfigurationPropertyAdapter<>(istProperties, "zeitTakt", Zeittakt.TAKT_10MS, sollProperties, Zeittakt.class);
    this.meldungsNegation = new ConfigurationPropertyAdapter<>(istProperties, "meldungsNegation", false, sollProperties, Boolean.class);
    this.meldungBeiZeStopp = new ConfigurationPropertyAdapter<>(istProperties, "meldungBeiZeStopp", MeldungsModus.UNVERAENDERT, sollProperties, MeldungsModus.class);
    this.meldungBeiFehlendemFahrstrom = new ConfigurationPropertyAdapter<>(istProperties, "meldungBeiFehlendemFahrstrom", MeldungsModus.UNVERAENDERT, sollProperties, MeldungsModus.class);
    this.meldungsSpeicherung = new ConfigurationPropertyAdapter<>(istProperties, "meldungsSpeicherung", MeldungsSpeicherung.ZE_STOPP, sollProperties, MeldungsSpeicherung.class);
  }

  /**
   * Wert liefern: {@link #ansprechVerzoegerung}.
   * 
   * @return Wert
   */
  public ConfigurationPropertyAdapter<Integer> getAnsprechVerzoegerung()
  {
    return this.ansprechVerzoegerung;
  }

  /**
   * Wert liefern: {@link #abfallVerzoegerung}.
   * 
   * @return Wert
   */
  public ConfigurationPropertyAdapter<Integer> getAbfallVerzoegerung()
  {
    return this.abfallVerzoegerung;
  }

  /**
   * Wert liefern: {@link #zeittakt}.
   * 
   * @return Wert
   */
  public ConfigurationPropertyAdapter<Zeittakt> getZeittakt()
  {
    return this.zeittakt;
  }

  /**
   * Wert liefern: {@link #meldungsNegation}.
   * 
   * @return Wert
   */
  public ConfigurationPropertyAdapter<Boolean> getMeldungsNegation()
  {
    return this.meldungsNegation;
  }

  /**
   * Wert liefern: {@link #meldungBeiZeStopp}.
   * 
   * @return Wert
   */
  public ConfigurationPropertyAdapter<MeldungsModus> getMeldungBeiZeStopp()
  {
    return this.meldungBeiZeStopp;
  }

  /**
   * Wert liefern: {@link #meldungBeiFehlendemFahrstrom}.
   * 
   * @return Wert
   */
  public ConfigurationPropertyAdapter<MeldungsModus> getMeldungBeiFehlendemFahrstrom()
  {
    return this.meldungBeiFehlendemFahrstrom;
  }

  /**
   * Wert liefern: {@link #meldungsSpeicherung}.
   * 
   * @return Wert
   */
  public ConfigurationPropertyAdapter<MeldungsSpeicherung> getMeldungsSpeicherung()
  {
    return this.meldungsSpeicherung;
  }

  public static BMMiba3ConfigurationAdapter createInstance(Baustein baustein, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration)
  {
    return new BMMiba3ConfigurationAdapter(istConfiguration, sollConfiguration);
  }

  public static enum MeldungsModus
  {
    STANDARD
    {
      @Override
      public int getBits()
      {
        return 0b00;
      }
    },
    UNVERAENDERT
    {
      @Override
      public int getBits()
      {
        return 0b10;
      }
    },
    GLEISFREI
    {
      @Override
      public int getBits()
      {
        return 0b01;
      }
    },
    GLEISBELEGT
    {
      @Override
      public int getBits()
      {
        return 0b11;
      }
    };

    public static MeldungsModus valueOf(int bits)
    {
      for (MeldungsModus meldungsModus : MeldungsModus.values())
      {
        if (meldungsModus.getBits() == bits)
        {
          return meldungsModus;
        }
      }
      throw new IllegalArgumentException("Ungueltige Meldungsmodus-Bits");
    }

    public abstract int getBits();
  }

  public static enum Zeittakt
  {
    TAKT_10MS
    {
      @Override
      public int getBits()
      {
        return 0b00;
      }
    },
    TAKT_20MS
    {
      @Override
      public int getBits()
      {
        return 0b10;
      }
    },
    TAKT_40MS
    {
      @Override
      public int getBits()
      {
        return 0b01;
      }
    },
    TAKT_80MS
    {
      @Override
      public int getBits()
      {
        return 0b11;
      }
    };

    public static Zeittakt valueof(int bits)
    {
      for (Zeittakt zeittakt : Zeittakt.values())
      {
        if (zeittakt.getBits() == bits)
        {
          return zeittakt;
        }
      }
      throw new IllegalArgumentException("Ungueltige Zeittakt-Bits");
    }

    public abstract int getBits();
  }

  public static enum MeldungsSpeicherung
  {
    IMMER
    {
      @Override
      public int getBits()
      {
        return 0b0;
      }
    },
    ZE_STOPP
    {
      @Override
      public int getBits()
      {
        return 0b1;
      }
    };

    public static MeldungsSpeicherung valueof(int bits)
    {
      for (MeldungsSpeicherung meldungsSpeicherung : MeldungsSpeicherung.values())
      {
        if (meldungsSpeicherung.getBits() == bits)
        {
          return meldungsSpeicherung;
        }
      }
      throw new IllegalArgumentException("Ungueltige MeldungsSpeicherung-Bits");
    }

    public abstract int getBits();
  }

}

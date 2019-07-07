package de.gedoplan.v5t11.status.service.besetztmelder.muet8i;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.service.ConfigurationAdapter;

import lombok.Getter;

/**
 * Konfigurations-Adapter für Besetztmelder des Typs Muet 8i.
 *
 * @author dw
 */
@Getter
public class Muet8iConfigurationAdapter extends ConfigurationAdapter {
  private ConfigurationPropertyAdapter<Integer> abfallVerzoegerung;
  private ConfigurationPropertyAdapter<MeldungsModus> meldungBeiZeStopp;
  private ConfigurationPropertyAdapter<Boolean> meldungsNegation;

  public Muet8iConfigurationAdapter(BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    super(istConfiguration, sollConfiguration);

    this.abfallVerzoegerung = new ConfigurationPropertyAdapter<>(this.istProperties, "abfallVerzoegerung", 50, this.sollProperties, Integer.class);
    this.meldungsNegation = new ConfigurationPropertyAdapter<>(this.istProperties, "meldungsNegation", false, this.sollProperties, Boolean.class);
    this.meldungBeiZeStopp = new ConfigurationPropertyAdapter<>(this.istProperties, "meldungBeiZeStopp", MeldungsModus.UNVERAENDERT, this.sollProperties, MeldungsModus.class);
  }

  public static enum MeldungsModus {
    UNVERAENDERT {
      @Override
      public int getBits() {
        return 0b0100_0000;
      }
    },
    GLEISFREI {
      @Override
      public int getBits() {
        return 0b0000_0000;
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

}

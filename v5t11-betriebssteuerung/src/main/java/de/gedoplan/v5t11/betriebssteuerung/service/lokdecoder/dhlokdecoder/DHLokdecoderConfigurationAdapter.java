package de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.dhlokdecoder;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.sxlokdecoder.SxLokdecoderConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Lokdecoder;

import lombok.Getter;

/**
 * Konfigurations-Adapter für Lokdecoder von D&H mit ohne erweiterten Parametern.
 *
 * @author dw
 */
@Getter
public class DHLokdecoderConfigurationAdapter extends SxLokdecoderConfigurationAdapter {
  protected ConfigurationPropertyAdapter<Boolean> anschlusstauschMotor;
  protected ConfigurationPropertyAdapter<Boolean> anschlusstauschLicht;
  protected ConfigurationPropertyAdapter<Boolean> anschlusstauschGleis;
  protected ConfigurationPropertyAdapter<RegelVariante> regelVariante;

  public DHLokdecoderConfigurationAdapter(Lokdecoder lokdecoder, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    super(lokdecoder, istConfiguration, sollConfiguration);

    this.anschlusstauschMotor = new ConfigurationPropertyAdapter<>(this.istProperties, "anschlusstauschMotor", false, this.sollProperties, Boolean.class);
    this.anschlusstauschLicht = new ConfigurationPropertyAdapter<>(this.istProperties, "anschlusstauschLicht", false, this.sollProperties, Boolean.class);
    this.anschlusstauschGleis = new ConfigurationPropertyAdapter<>(this.istProperties, "anschlusstauschGleis", false, this.sollProperties, Boolean.class);
    this.regelVariante = new ConfigurationPropertyAdapter<>(this.istProperties, "regelVariante", RegelVariante.WEICH, this.sollProperties, RegelVariante.class);
  }

  public static DHLokdecoderConfigurationAdapter createInstance(Baustein baustein, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    return new DHLokdecoderConfigurationAdapter((Lokdecoder) baustein, istConfiguration, sollConfiguration);
  }

  public void resetAnschlusstauschToSoll() {
    this.anschlusstauschMotor.resetToSoll();
    this.anschlusstauschLicht.resetToSoll();
    this.anschlusstauschGleis.resetToSoll();
  }

  public static enum RegelVariante {
    SEHR_HART {
      @Override
      public int getBits() {
        return 0b001;
      }
    },
    HART {
      @Override
      public int getBits() {
        return 0b010;
      }
    },
    WEICH {
      @Override
      public int getBits() {
        return 0b011;
      }
    },
    SEHR_WEICH {
      @Override
      public int getBits() {
        return 0b100;
      }
    };

    public static RegelVariante valueOf(int bits) {
      for (RegelVariante impulsbreite : RegelVariante.values()) {
        if (impulsbreite.getBits() == bits) {
          return impulsbreite;
        }
      }
      throw new IllegalArgumentException("Ungueltige RegelVariante-Bits");
    }

    public abstract int getBits();
  }
}
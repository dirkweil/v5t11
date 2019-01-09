package de.gedoplan.v5t11.status.service.lokdecoder.dhlokdecoder;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.entity.baustein.Baustein;
import de.gedoplan.v5t11.status.entity.baustein.Lokdecoder;
import de.gedoplan.v5t11.status.service.lokdecoder.LokdecoderConfigurationAdapter;

import lombok.Getter;

/**
 * Konfigurations-Adapter für Lokdecoder von D&H mit ohne erweiterten Parametern.
 *
 * @author dw
 */
@Getter
public class DHLokdecoderConfigurationAdapter extends LokdecoderConfigurationAdapter {
  private ConfigurationPropertyAdapter<Boolean> anschlusstauschMotor;
  private ConfigurationPropertyAdapter<Boolean> anschlusstauschLicht;
  private ConfigurationPropertyAdapter<Boolean> anschlusstauschGleis;
  private ConfigurationPropertyAdapter<RegelVariante> regelVariante;

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
        return 0b00;
      }
    },
    HART {
      @Override
      public int getBits() {
        return 0b01;
      }
    },
    WEICH {
      @Override
      public int getBits() {
        return 0b10;
      }
    },
    SEHR_WEICH {
      @Override
      public int getBits() {
        return 0b11;
      }
    };

    public static RegelVariante valueOf(int bits) {
      for (RegelVariante impulsbreite : RegelVariante.values()) {
        if (impulsbreite.getBits() == bits) {
          return impulsbreite;
        }
      }
      throw new IllegalArgumentException("Ungueltige RegelVariante-Bits0b" + Integer.toBinaryString(bits));
    }

    public abstract int getBits();
  }
}

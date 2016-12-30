package de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Lokdecoder;

import lombok.Getter;

/**
 * Konfigurations-Adapter für Lokdecoder.
 *
 * @author dw
 */
@Getter
public abstract class LokdecoderConfigurationAdapter extends ConfigurationAdapter {
  protected Lokdecoder lokdecoder;

  protected ConfigurationPropertyAdapter<Impulsbreite> impulsbreite;
  protected ConfigurationPropertyAdapter<Integer> traegheit;
  protected ConfigurationPropertyAdapter<Integer> hoechstGeschwindigkeit;

  public LokdecoderConfigurationAdapter(Lokdecoder lokdecoder, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    super(istConfiguration, sollConfiguration);
    this.lokdecoder = lokdecoder;

    this.impulsbreite = new ConfigurationPropertyAdapter<>(this.istProperties, "impulsbreite", Impulsbreite.KLEIN, this.sollProperties, Impulsbreite.class);
    this.traegheit = new ConfigurationPropertyAdapter<>(this.istProperties, "traegheit", 4, this.sollProperties, Integer.class);
    this.hoechstGeschwindigkeit = new ConfigurationPropertyAdapter<>(this.istProperties, "hoechstGeschwindigkeit", 5, this.sollProperties, Integer.class);
  }

  public Lokdecoder getlokdecoder() {
    return this.lokdecoder;
  }

  public static enum Impulsbreite {
    MINIMAL {
      @Override
      public int getBits() {
        return 0b00;
      }
    },
    KLEIN {
      @Override
      public int getBits() {
        return 0b01;
      }
    },
    GROSS {
      @Override
      public int getBits() {
        return 0b10;
      }
    },
    MAXIMAL {
      @Override
      public int getBits() {
        return 0b11;
      }
    };

    public static Impulsbreite valueOf(int bits) {
      for (Impulsbreite impulsbreite : Impulsbreite.values()) {
        if (impulsbreite.getBits() == bits) {
          return impulsbreite;
        }
      }
      throw new IllegalArgumentException("Ungueltige Impulsbreite-Bits");
    }

    public abstract int getBits();
  }

}

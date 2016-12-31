package de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.sxlokdecoder;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.LokdecoderConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Lokdecoder;

import lombok.Getter;

/**
 * Konfigurations-Adapter für Lokdecoder der einfachen Typen Trix 66830, 66832 etc. ohne erweiterte Parameter.
 *
 * @author dw
 */
@Getter
public class SxLokdecoderConfigurationAdapter extends LokdecoderConfigurationAdapter {
  protected ConfigurationPropertyAdapter<Impulsbreite> impulsbreite;
  protected ConfigurationPropertyAdapter<Integer> traegheit;
  protected ConfigurationPropertyAdapter<Integer> hoechstGeschwindigkeit;

  public SxLokdecoderConfigurationAdapter(Lokdecoder lokdecoder, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    super(lokdecoder, istConfiguration, sollConfiguration);

    this.impulsbreite = new ConfigurationPropertyAdapter<>(this.istProperties, "impulsbreite", Impulsbreite.KLEIN, this.sollProperties, Impulsbreite.class);
    this.traegheit = new ConfigurationPropertyAdapter<>(this.istProperties, "traegheit", 4, this.sollProperties, Integer.class, Integer::valueOf,
        () -> new Integer[] { 1, 2, 3, 4, 5, 6, 7 });
    this.hoechstGeschwindigkeit = new ConfigurationPropertyAdapter<>(this.istProperties, "hoechstGeschwindigkeit", 5, this.sollProperties, Integer.class, Integer::valueOf,
        () -> new Integer[] { 1, 2, 3, 4, 5, 6, 7 });

  }

  public static SxLokdecoderConfigurationAdapter createInstance(Baustein baustein, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    return new SxLokdecoderConfigurationAdapter((Lokdecoder) baustein, istConfiguration, sollConfiguration);
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

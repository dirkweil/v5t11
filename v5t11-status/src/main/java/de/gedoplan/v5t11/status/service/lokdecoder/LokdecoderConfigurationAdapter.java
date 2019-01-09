package de.gedoplan.v5t11.status.service.lokdecoder;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.entity.baustein.Lokdecoder;
import de.gedoplan.v5t11.status.service.ConfigurationAdapter;

import lombok.Getter;

/**
 * Konfigurations-Adapter für Lokdecoder.
 * Enthält die Standard-Parameter für alle Selectrix-Decoder.
 *
 * @author dw
 */
@Getter
public class LokdecoderConfigurationAdapter extends ConfigurationAdapter {
  protected Lokdecoder lokdecoder;

  protected ConfigurationPropertyAdapter<Impulsbreite> impulsbreite;
  protected ConfigurationPropertyAdapter<Integer> traegheit;
  protected ConfigurationPropertyAdapter<Integer> hoechstGeschwindigkeit;
  protected ConfigurationPropertyAdapter<Boolean> mehrteiligerHalteabschnitt;

  public LokdecoderConfigurationAdapter(Lokdecoder lokdecoder, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    super(istConfiguration, sollConfiguration);
    this.lokdecoder = lokdecoder;

    this.impulsbreite = new ConfigurationPropertyAdapter<>(this.istProperties, "impulsbreite", Impulsbreite.KLEIN, this.sollProperties, Impulsbreite.class);
    this.traegheit = new ConfigurationPropertyAdapter<>(this.istProperties, "traegheit", 4, this.sollProperties, Integer.class, Integer::valueOf,
        () -> new Integer[] { 1, 2, 3, 4, 5, 6, 7 });
    this.hoechstGeschwindigkeit = new ConfigurationPropertyAdapter<>(this.istProperties, "hoechstGeschwindigkeit", 5, this.sollProperties, Integer.class, Integer::valueOf,
        () -> new Integer[] { 1, 2, 3, 4, 5, 6, 7 });
    this.mehrteiligerHalteabschnitt = new ConfigurationPropertyAdapter<>(this.istProperties, "mehrteiligerHalteabschnitt", false, this.sollProperties, Boolean.class);
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
      throw new IllegalArgumentException("Ungueltige Impulsbreite-Bits: 0b" + Integer.toBinaryString(bits));
    }

    public abstract int getBits();
  }
}

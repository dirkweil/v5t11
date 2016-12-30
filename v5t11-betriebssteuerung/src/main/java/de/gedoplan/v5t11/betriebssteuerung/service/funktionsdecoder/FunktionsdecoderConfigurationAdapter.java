package de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Geraet;

import lombok.Getter;
import lombok.Setter;

/**
 * Konfigurations-Adapter für Funktionsdecoder.
 *
 * @author dw
 */
@Getter
public abstract class FunktionsdecoderConfigurationAdapter extends ConfigurationAdapter {
  private Funktionsdecoder funktionsdecoder;

  private DauerConfiguration[] dauer;

  /**
   * @param funktionsdecoder
   */
  public FunktionsdecoderConfigurationAdapter(Funktionsdecoder funktionsdecoder, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    super(istConfiguration, sollConfiguration);
    this.funktionsdecoder = funktionsdecoder;

    this.dauer = new DauerConfiguration[funktionsdecoder.getByteAnzahl() * 8];
    for (int i = 0; i < this.dauer.length; ++i) {
      this.dauer[i] = new DauerConfiguration();
    }

    for (Geraet geraet : funktionsdecoder.getGeraete()) {
      int anschluss = geraet.getAnschluss();
      int offset = geraet.getBitCount();
      while (offset > 0) {
        --offset;
        this.dauer[anschluss + offset].setSoll(geraet.isDauer());
      }
    }
  }

  public void resetDauerToSoll() {
    for (int i = 0; i < this.dauer.length; ++i) {
      this.dauer[i].setIst(this.dauer[i].isSoll());
    }
  }

  @Getter
  @Setter
  public static class DauerConfiguration {
    private boolean ist;
    private boolean soll;
  }
}

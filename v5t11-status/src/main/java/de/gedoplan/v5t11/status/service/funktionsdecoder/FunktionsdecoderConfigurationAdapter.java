package de.gedoplan.v5t11.status.service.funktionsdecoder;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.entity.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.status.service.ConfigurationAdapter;

import lombok.Getter;

/**
 * Konfigurations-Adapter für Funktionsdecoder.
 *
 * @author dw
 */
@Getter
public abstract class FunktionsdecoderConfigurationAdapter extends ConfigurationAdapter {
  private Funktionsdecoder funktionsdecoder;

  private ConfigurationPropertyAdapter<Boolean> dauer[];

  /**
   * @param funktionsdecoder
   */
  @SuppressWarnings("unchecked")
  public FunktionsdecoderConfigurationAdapter(Funktionsdecoder funktionsdecoder, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    super(istConfiguration, sollConfiguration);
    this.funktionsdecoder = funktionsdecoder;

    this.dauer = new ConfigurationPropertyAdapter[funktionsdecoder.getByteAnzahl() * 8];
    for (int i = 0; i < this.dauer.length; ++i) {
      this.dauer[i] = new ConfigurationPropertyAdapter<>(this.istProperties, "dauer_" + i, false, this.sollProperties, Boolean.class);
    }
  }

  public void resetDauerToSoll() {
    for (int i = 0; i < this.dauer.length; ++i) {
      this.dauer[i].setIst(this.dauer[i].getSoll());
    }
  }
}

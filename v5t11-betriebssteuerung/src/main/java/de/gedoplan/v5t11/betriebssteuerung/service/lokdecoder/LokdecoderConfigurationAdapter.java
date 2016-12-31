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

  public LokdecoderConfigurationAdapter(Lokdecoder lokdecoder, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    super(istConfiguration, sollConfiguration);
    this.lokdecoder = lokdecoder;
  }
}

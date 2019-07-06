package de.gedoplan.v5t11.status.service.funktionsdecoder.strfd1;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.entity.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.status.service.funktionsdecoder.FunktionsdecoderConfigurationAdapter;

import lombok.Getter;

/**
 * Konfigurations-Adapter für Funktionsdecoder des Typs STR-FD 1.
 *
 * @author dw
 */
@Getter
public class STRFD1ConfigurationAdapter extends FunktionsdecoderConfigurationAdapter {
  private ConfigurationPropertyAdapter<Integer> impulsDauer;

  public STRFD1ConfigurationAdapter(Funktionsdecoder funktionsdecoder, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    super(funktionsdecoder, istConfiguration, sollConfiguration);

    this.impulsDauer = new ConfigurationPropertyAdapter<>(this.istProperties, "impulsDauer", 16 * 80, this.sollProperties, Integer.class);
  }
}

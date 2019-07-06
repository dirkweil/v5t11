package de.gedoplan.v5t11.status.service.funktionsdecoder.wdmiba3;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.entity.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.status.service.funktionsdecoder.FunktionsdecoderConfigurationAdapter;

import lombok.Getter;

/**
 * Konfigurations-Adapter für Funktionsdecoder des Typs WD-MIBA 3.
 *
 * @author dw
 */
@Getter
public class WDMiba3ConfigurationAdapter extends FunktionsdecoderConfigurationAdapter {
  private ConfigurationPropertyAdapter<Integer> impulsDauer;

  public WDMiba3ConfigurationAdapter(Funktionsdecoder funktionsdecoder, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    super(funktionsdecoder, istConfiguration, sollConfiguration);

    this.impulsDauer = new ConfigurationPropertyAdapter<>(this.istProperties, "impulsDauer", 16 * 80, this.sollProperties, Integer.class);
  }

  // public static WDMiba3ConfigurationAdapter createInstance(Baustein baustein, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
  // return new WDMiba3ConfigurationAdapter((Funktionsdecoder) baustein, istConfiguration, sollConfiguration);
  // }
}

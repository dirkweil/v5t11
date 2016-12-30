package de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.wdmiba3;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.FunktionsdecoderConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Funktionsdecoder;

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

  public static WDMiba3ConfigurationAdapter createInstance(Baustein baustein, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    return new WDMiba3ConfigurationAdapter((Funktionsdecoder) baustein, istConfiguration, sollConfiguration);
  }
}

package de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.wdmiba3;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.FunktionsdecoderConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Funktionsdecoder;

import java.util.Collections;
import java.util.Map;

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

    Map<String, String> istProperties = this.istConfiguration.getProperties();
    Map<String, String> sollProperties = null;
    if (this.sollConfiguration != null) {
      sollProperties = this.sollConfiguration.getProperties();
    }
    if (sollProperties == null) {
      sollProperties = Collections.emptyMap();
    }

    this.impulsDauer = new ConfigurationPropertyAdapter<>(istProperties, "impulsDauer", 16 * 80, sollProperties, Integer.class);
  }

  public static WDMiba3ConfigurationAdapter createInstance(Baustein baustein, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    return new WDMiba3ConfigurationAdapter((Funktionsdecoder) baustein, istConfiguration, sollConfiguration);
  }
}

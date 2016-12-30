package de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.sxsd1;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.FunktionsdecoderConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Funktionsdecoder;

import lombok.Getter;

/**
 * Konfigurations-Adapter für Funktionsdecoder des Typs SX-SD 1.
 *
 * @author dw
 */
@Getter
public class SXSD1ConfigurationAdapter extends FunktionsdecoderConfigurationAdapter {
  public SXSD1ConfigurationAdapter(Funktionsdecoder funktionsdecoder, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    super(funktionsdecoder, istConfiguration, sollConfiguration);
  }

  public static SXSD1ConfigurationAdapter createInstance(Baustein baustein, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    return new SXSD1ConfigurationAdapter((Funktionsdecoder) baustein, istConfiguration, sollConfiguration);
  }
}

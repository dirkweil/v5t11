package de.gedoplan.v5t11.status.service.funktionsdecoder.wdmiba;

import de.gedoplan.v5t11.status.entity.BausteinConfiguration;
import de.gedoplan.v5t11.status.entity.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.status.service.funktionsdecoder.FunktionsdecoderConfigurationAdapter;

import lombok.Getter;

/**
 * Konfigurations-Adapter für Funktionsdecoder des Typs WD-MIBA.
 *
 * @author dw
 */
@Getter
public class WDMibaConfigurationAdapter extends FunktionsdecoderConfigurationAdapter {
  public WDMibaConfigurationAdapter(Funktionsdecoder funktionsdecoder, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    super(funktionsdecoder, istConfiguration, sollConfiguration);
  }

  // public static WDMibaConfigurationAdapter createInstance(Baustein baustein, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
  // return new WDMibaConfigurationAdapter((Funktionsdecoder) baustein, istConfiguration, sollConfiguration);
  // }
}

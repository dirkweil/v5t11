package de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.tr66830;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.LokdecoderConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Lokdecoder;

import lombok.Getter;

/**
 * Konfigurations-Adapter für Lokdecoder des Typs Trix 66830.
 *
 * @author dw
 */
@Getter
public class Tr66830ConfigurationAdapter extends LokdecoderConfigurationAdapter {
  public Tr66830ConfigurationAdapter(Lokdecoder lokdecoder, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    super(lokdecoder, istConfiguration, sollConfiguration);
  }

  public static Tr66830ConfigurationAdapter createInstance(Baustein baustein, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    return new Tr66830ConfigurationAdapter((Lokdecoder) baustein, istConfiguration, sollConfiguration);
  }
}

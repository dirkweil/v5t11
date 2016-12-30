package de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.sxlokdecoder;

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
public class SxLokdecoderConfigurationAdapter extends LokdecoderConfigurationAdapter {
  public SxLokdecoderConfigurationAdapter(Lokdecoder lokdecoder, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    super(lokdecoder, istConfiguration, sollConfiguration);
  }

  public static SxLokdecoderConfigurationAdapter createInstance(Baustein baustein, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration) {
    return new SxLokdecoderConfigurationAdapter((Lokdecoder) baustein, istConfiguration, sollConfiguration);
  }
}

package de.gedoplan.v5t11.betriebssteuerung.service;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Funktionsdecoder;

public class SXSD1ConfigurationAdapter extends FunktionsdecoderConfigurationAdapter
{
  public SXSD1ConfigurationAdapter(Funktionsdecoder funktionsdecoder, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration)
  {
    super(funktionsdecoder, istConfiguration, sollConfiguration);
  }

  public static SXSD1ConfigurationAdapter createInstance(Baustein baustein, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration)
  {
    return new SXSD1ConfigurationAdapter((Funktionsdecoder) baustein, istConfiguration, sollConfiguration);
  }
}

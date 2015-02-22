package de.gedoplan.v5t11.betriebssteuerung.service;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Lokdecoder;

public class DHL100ConfigurationAdapter extends LokdecoderConfigurationAdapter
{
  public DHL100ConfigurationAdapter(Lokdecoder lokdecoder, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration)
  {
    super(lokdecoder, istConfiguration, sollConfiguration);
  }

  public static DHL100ConfigurationAdapter createInstance(Baustein baustein, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration)
  {
    return new DHL100ConfigurationAdapter((Lokdecoder) baustein, istConfiguration, sollConfiguration);
  }
}

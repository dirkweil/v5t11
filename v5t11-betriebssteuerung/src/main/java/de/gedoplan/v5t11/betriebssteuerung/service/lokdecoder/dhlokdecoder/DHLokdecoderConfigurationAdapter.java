package de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.dhlokdecoder;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.lokdecoder.LokdecoderConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Lokdecoder;

public class DHLokdecoderConfigurationAdapter extends LokdecoderConfigurationAdapter
{
  public DHLokdecoderConfigurationAdapter(Lokdecoder lokdecoder, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration)
  {
    super(lokdecoder, istConfiguration, sollConfiguration);
  }

  public static DHLokdecoderConfigurationAdapter createInstance(Baustein baustein, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration)
  {
    return new DHLokdecoderConfigurationAdapter((Lokdecoder) baustein, istConfiguration, sollConfiguration);
  }
}

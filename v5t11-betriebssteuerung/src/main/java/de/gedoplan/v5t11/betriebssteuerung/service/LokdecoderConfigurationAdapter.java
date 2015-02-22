package de.gedoplan.v5t11.betriebssteuerung.service;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Lokdecoder;

public class LokdecoderConfigurationAdapter extends ConfigurationAdapter
{
  private Lokdecoder lokdecoder;

  public LokdecoderConfigurationAdapter(Lokdecoder lokdecoder, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration)
  {
    super(istConfiguration, sollConfiguration);
    this.lokdecoder = lokdecoder;
  }

  public Lokdecoder getlokdecoder()
  {
    return this.lokdecoder;
  }

}

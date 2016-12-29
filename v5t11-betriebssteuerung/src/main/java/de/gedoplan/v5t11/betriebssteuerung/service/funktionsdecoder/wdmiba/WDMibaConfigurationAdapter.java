package de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.wdmiba;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder.FunktionsdecoderConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Funktionsdecoder;

public class WDMibaConfigurationAdapter extends FunktionsdecoderConfigurationAdapter
{
  public WDMibaConfigurationAdapter(Funktionsdecoder funktionsdecoder, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration)
  {
    super(funktionsdecoder, istConfiguration, sollConfiguration);
  }

  public static WDMibaConfigurationAdapter createInstance(Baustein baustein, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration)
  {
    return new WDMibaConfigurationAdapter((Funktionsdecoder) baustein, istConfiguration, sollConfiguration);
  }
}

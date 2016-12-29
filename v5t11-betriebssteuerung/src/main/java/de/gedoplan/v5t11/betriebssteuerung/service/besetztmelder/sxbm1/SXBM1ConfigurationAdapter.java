package de.gedoplan.v5t11.betriebssteuerung.service.besetztmelder.sxbm1;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationPropertyAdapter;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;

import java.util.Collections;
import java.util.Map;

public class SXBM1ConfigurationAdapter extends ConfigurationAdapter
{
  private ConfigurationPropertyAdapter<Integer> abfallVerzoegerung;

  /**
   * @param istConfiguration
   * @param sollConfiguration
   */
  public SXBM1ConfigurationAdapter(BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration)
  {
    super(istConfiguration, sollConfiguration);

    Map<String, String> istProperties = this.istConfiguration.getProperties();
    Map<String, String> sollProperties = null;
    if (this.sollConfiguration != null)
    {
      sollProperties = this.sollConfiguration.getProperties();
    }
    if (sollProperties == null)
    {
      sollProperties = Collections.emptyMap();
    }

    this.abfallVerzoegerung = new ConfigurationPropertyAdapter<>(istProperties, "abfallVerzoegerung", 500, sollProperties, Integer.class);
  }

  /**
   * Wert liefern: {@link #abfallVerzoegerung}.
   * 
   * @return Wert
   */
  public ConfigurationPropertyAdapter<Integer> getAbfallVerzoegerung()
  {
    return this.abfallVerzoegerung;
  }

  public static SXBM1ConfigurationAdapter createInstance(Baustein baustein, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration)
  {
    return new SXBM1ConfigurationAdapter(istConfiguration, sollConfiguration);
  }
}

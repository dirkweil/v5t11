package de.gedoplan.v5t11.betriebssteuerung.service;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Baustein;

import java.util.Collections;
import java.util.Map;

public class SD8ConfigurationAdapter extends ConfigurationAdapter
{
  private ServoConfiguration[]                  servoConfiguration;
  private ConfigurationPropertyAdapter<Integer> abschaltZeit;

  /**
   * @param istConfiguration
   * @param sollConfiguration
   */
  public SD8ConfigurationAdapter(BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration)
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

    this.servoConfiguration = new ServoConfiguration[8];
    for (int servoNummer = 1; servoNummer <= 8; ++servoNummer)
    {
      this.servoConfiguration[servoNummer - 1] = new ServoConfiguration(servoNummer, istProperties, sollProperties);
    }

    this.abschaltZeit = new ConfigurationPropertyAdapter<Integer>(istProperties, "abschaltZeit", 0, sollProperties, Integer.class);
  }

  /**
   * Wert liefern: {@link #servoConfiguration}.
   * 
   * @return Wert
   */
  public ServoConfiguration[] getServo()
  {
    return this.servoConfiguration;
  }

  /**
   * Wert liefern: {@link #abschaltZeit}.
   * 
   * @return Wert
   */
  public ConfigurationPropertyAdapter<Integer> getAbschaltZeit()
  {
    return this.abschaltZeit;
  }

  public static SD8ConfigurationAdapter createInstance(Baustein baustein, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration)
  {
    return new SD8ConfigurationAdapter(istConfiguration, sollConfiguration);
  }

  public static class ServoConfiguration
  {
    private ConfigurationPropertyAdapter<Integer> start;
    private ConfigurationPropertyAdapter<Integer> ende;
    private ConfigurationPropertyAdapter<Integer> geschwindigkeit;
    private int                                   servoNummer;

    /**
     * 
     */
    public ServoConfiguration(int servoNummer, Map<String, String> istProperties, Map<String, String> sollProperties)
    {
      this.servoNummer = servoNummer;
      this.start = new ConfigurationPropertyAdapter<>(istProperties, "start_" + servoNummer, 50, sollProperties, Integer.class);
      this.ende = new ConfigurationPropertyAdapter<>(istProperties, "ende_" + servoNummer, 80, sollProperties, Integer.class);
      this.geschwindigkeit = new ConfigurationPropertyAdapter<>(istProperties, "geschwindigkeit_" + servoNummer, 50, sollProperties, Integer.class);
    }

    /**
     * Wert liefern: {@link #servoNummer}.
     * 
     * @return Wert
     */
    public int getServoNummer()
    {
      return this.servoNummer;
    }

    /**
     * Wert liefern: {@link #start}.
     * 
     * @return Wert
     */
    public ConfigurationPropertyAdapter<Integer> getStart()
    {
      return this.start;
    }

    /**
     * Wert liefern: {@link #ende}.
     * 
     * @return Wert
     */
    public ConfigurationPropertyAdapter<Integer> getEnde()
    {
      return this.ende;
    }

    /**
     * Wert liefern: {@link #geschwindigkeit}.
     * 
     * @return Wert
     */
    public ConfigurationPropertyAdapter<Integer> getGeschwindigkeit()
    {
      return this.geschwindigkeit;
    }

    public void resetToSoll()
    {
      this.start.resetToSoll();
      this.ende.resetToSoll();
      this.geschwindigkeit.resetToSoll();
    }

  }
}

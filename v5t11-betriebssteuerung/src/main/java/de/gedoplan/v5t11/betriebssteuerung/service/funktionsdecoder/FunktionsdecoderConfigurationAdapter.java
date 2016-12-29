package de.gedoplan.v5t11.betriebssteuerung.service.funktionsdecoder;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;
import de.gedoplan.v5t11.betriebssteuerung.service.ConfigurationAdapter;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Geraet;

public class FunktionsdecoderConfigurationAdapter extends ConfigurationAdapter
{
  private Funktionsdecoder     funktionsdecoder;

  private DauerConfiguration[] dauer;

  /**
   * @param funktionsdecoder
   */
  public FunktionsdecoderConfigurationAdapter(Funktionsdecoder funktionsdecoder, BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration)
  {
    super(istConfiguration, sollConfiguration);
    this.funktionsdecoder = funktionsdecoder;

    this.dauer = new DauerConfiguration[funktionsdecoder.getByteAnzahl() * 8];
    for (int i = 0; i < this.dauer.length; ++i)
    {
      this.dauer[i] = new DauerConfiguration();
    }

    for (Geraet geraet : funktionsdecoder.getGeraete())
    {
      int anschluss = geraet.getAnschluss();
      int offset = geraet.getBitCount();
      while (offset > 0)
      {
        --offset;
        this.dauer[anschluss + offset].setSoll(geraet.isDauer());
      }
    }
  }

  /**
   * Wert liefern: {@link #funktionsdecoder}.
   * 
   * @return Wert
   */
  public Funktionsdecoder getFunktionsdecoder()
  {
    return this.funktionsdecoder;
  }

  /**
   * Wert liefern: {@link #dauer}.
   * 
   * @return Wert
   */
  public DauerConfiguration[] getDauer()
  {
    return this.dauer;
  }

  public void resetDauerToSoll()
  {
    for (int i = 0; i < this.dauer.length; ++i)
    {
      this.dauer[i].setIst(this.dauer[i].isSoll());
    }
  }

  public static class DauerConfiguration
  {
    private boolean ist;
    private boolean soll;

    /**
     * Wert liefern: {@link #ist}.
     * 
     * @return Wert
     */
    public boolean isIst()
    {
      return this.ist;
    }

    /**
     * Wert setzen: {@link #ist}.
     * 
     * @param ist Wert
     */
    public void setIst(boolean ist)
    {
      this.ist = ist;
    }

    /**
     * Wert liefern: {@link #soll}.
     * 
     * @return Wert
     */
    public boolean isSoll()
    {
      return this.soll;
    }

    /**
     * Wert setzen: {@link #soll}.
     * 
     * @param soll Wert
     */
    public void setSoll(boolean soll)
    {
      this.soll = soll;
    }

  }
}

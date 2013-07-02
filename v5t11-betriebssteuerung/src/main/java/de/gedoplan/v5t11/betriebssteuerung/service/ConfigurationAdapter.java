package de.gedoplan.v5t11.betriebssteuerung.service;

import de.gedoplan.v5t11.betriebssteuerung.entity.BausteinConfiguration;

public abstract class ConfigurationAdapter
{
  protected BausteinConfiguration sollConfiguration;
  protected BausteinConfiguration istConfiguration;
  protected boolean               adresseDirty;

  /**
   * @param istConfiguration
   * @param sollConfiguration
   */
  public ConfigurationAdapter(BausteinConfiguration istConfiguration, BausteinConfiguration sollConfiguration)
  {
    this.istConfiguration = istConfiguration;
    this.sollConfiguration = sollConfiguration;
  }

  /**
   * Wert liefern: {@link #sollConfiguration}.
   * 
   * @return Wert
   */
  public BausteinConfiguration getSollConfiguration()
  {
    return this.sollConfiguration;
  }

  /**
   * Wert liefern: {@link #istConfiguration}.
   * 
   * @return Wert
   */
  public BausteinConfiguration getIstConfiguration()
  {
    return this.istConfiguration;
  }

  public int getAdresseSoll()
  {
    return this.sollConfiguration.getAdresse();
  }

  public int getAdresseIst()
  {
    return this.istConfiguration.getAdresse();
  }

  public void setAdresseIst(int adresse)
  {
    if (adresse != this.istConfiguration.getAdresse())
    {
      this.istConfiguration.setAdresse(adresse);
      this.adresseDirty = true;
    }
  }

  public void adresseResetToSoll()
  {
    setAdresseIst(getAdresseSoll());
  }

  /**
   * Wert liefern: {@link #adresseDirty}.
   * 
   * @return Wert
   */
  public boolean isAdresseDirty()
  {
    return this.adresseDirty;
  }

  /**
   * Wert löschen: {@link #adresseDirty}.
   */
  public void clearAdresseDirty()
  {
    this.adresseDirty = false;
  }

}

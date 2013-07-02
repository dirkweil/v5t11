package de.gedoplan.v5t11.betriebssteuerung.messaging;

import java.io.Serializable;

public class LokControllerMessage implements Serializable
{
  private String lokControllerId;
  private int    lokAdresse;

  /**
   * @param lokControllerId
   * @param lokAdresse
   */
  public LokControllerMessage(String lokControllerId, int lokAdresse)
  {
    this.lokControllerId = lokControllerId;
    this.lokAdresse = lokAdresse;
  }

  /**
   * Wert liefern: {@link #lokControllerId}.
   * 
   * @return Wert
   */
  public String getLokControllerId()
  {
    return this.lokControllerId;
  }

  /**
   * Wert liefern: {@link #lokAdresse}.
   * 
   * @return Wert
   */
  public int getLokAdresse()
  {
    return this.lokAdresse;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return "LokControllerMessage [lokControllerId=" + this.lokControllerId + ", lokAdresse=" + this.lokAdresse + "]";
  }

}

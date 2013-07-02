package de.gedoplan.v5t11.selectrix;

import java.io.Serializable;

/**
 * Message für den Versand eines Selectrix-Adresswertes.
 * 
 * @author dw
 */
public class SelectrixMessage implements Serializable
{
  /**
   * Adresse (0..127).
   */
  private int address;

  /**
   * Wert (0..256).
   */
  private int value;

  /**
   * Konstruktor.
   */
  public SelectrixMessage()
  {
  }

  /**
   * Konstruktor.
   * 
   * @param address Adresse
   * @param value Wert
   */
  public SelectrixMessage(int address, int value)
  {
    this.address = address;
    this.value = value;
  }

  /**
   * Wert liefern: {@link #address}.
   * 
   * @return Wert
   */
  public int getAddress()
  {
    return this.address;
  }

  /**
   * Wert setzen: {@link #address}.
   * 
   * @param address Wert
   */
  public void setAddress(int address)
  {
    this.address = address;
  }

  /**
   * Wert liefern: {@link #value}.
   * 
   * @return Wert
   */
  public int getValue()
  {
    return this.value;
  }

  /**
   * Wert setzen: {@link #value}.
   * 
   * @param value Wert
   */
  public void setValue(int value)
  {
    this.value = value;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return String.format("SelectrixMessage [address=%d, value=0x%02x]", this.address, this.value);
  }
}

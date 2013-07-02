package de.gedoplan.v5t11.selectrix;

import java.io.IOException;
import java.util.Collection;

/**
 * Connection zum Selectrix-Adapter.
 * 
 * @author dw
 */
public interface SelectrixConnection extends AutoCloseable
{
  public static final int MAX_ADDRESSE = 127;

  /**
   * Zu überwachende Adressen hinzufügen. Der Lesethread beobachtet ggf. - abhängig von der konkreten Impolementierung - nur die
   * hiermit angemeldeten Adressen.
   * 
   * @param adressen Adressen (0..127).
   */
  public void addAdressen(Collection<Integer> adressen);

  /**
   * Selectrix-Adresswert abfragen.
   * 
   * @param address Adresse (0..127).
   * @return Wert (0..256)
   * @throws IOException
   */
  public int getValue(int address);

  /**
   * Selectrix-Adresswert abfragen.
   * 
   * @param address Adresse (0..127).
   * @param refresh Wenn <code>true</code>, Wert erneut lesen, sonst nur aus Cache liefern
   * @return Wert (0..256)
   * @throws IOException
   */
  public int getValue(int address, boolean refresh);

  /**
   * Selectrix-Adresswert setzen.
   * 
   * @param address Adresse (0..127).
   * @return Wert (0..256)
   * @throws IOException
   */
  public void setValue(int address, int value);

  /**
   * Connection schliessen.
   */
  @Override
  public void close();
}

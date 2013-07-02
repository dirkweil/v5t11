package de.gedoplan.v5t11.selectrix.impl;

import de.gedoplan.v5t11.selectrix.SelectrixConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Lesethread für das Selectrix-Interface.
 * 
 * @author dw
 */
public abstract class SelectrixWorker extends Thread
{
  protected Log           logger        = LogFactory.getLog(this.getClass());

  protected InputStream   in;

  protected OutputStream  out;

  protected Object        inOutLock     = new Object();

  protected AtomicBoolean stopRequested = new AtomicBoolean();

  protected Set<Integer>  adressen      = new CopyOnWriteArraySet<>();

  /**
   * Konstruktor.
   * 
   * @param in Input-Stream
   * @param out Output-Stream
   */
  public SelectrixWorker(InputStream in, OutputStream out)
  {
    if (this.logger.isDebugEnabled())
    {
      this.logger.debug("Starting worker " + this.getClass().getSimpleName());
    }

    this.in = in;
    this.out = out;

    this.stopRequested.set(false);
  }

  /**
   * Wert lesen.
   * 
   * @param adresse Adresse
   * @return Wert
   * @throws IOException bei Fehlern
   */
  protected abstract int read(int adresse) throws IOException;

  /**
   * Wert schreiben.
   * 
   * @param adresse Adresse
   * @param wert Wert
   * @throws IOException bei Fehlern
   */
  protected void write(int adresse, int wert) throws IOException
  {
    assert adresse >= 0 && adresse <= SelectrixConnection.MAX_ADDRESSE : "Ungueltige Adresse: " + adresse;
    assert wert >= 0 && wert < 256 : "Ungueltiger Wert: " + wert;

    if (this.logger.isTraceEnabled())
    {
      this.logger.trace("write(" + adresse + ", " + wert + ")");
    }

    synchronized (this.inOutLock)
    {
      byte[] buf = { (byte) (adresse | 0x80), (byte) wert };
      this.out.write(buf);
      this.out.flush();
    }
  }

  public void requestStop()
  {
    this.stopRequested.set(true);
    this.interrupt();
  }

  public boolean isStopRequested()
  {
    return this.stopRequested.get();
  }

  /**
   * Zu überwachende Adresse hinzufügen. Der Lesethread beobachtet ggf. - abhängig von der konkreten Impolementierung - nur die
   * hiermit angemeldeten Adressen.
   * 
   * @param address Adresse
   */
  public void addWatchAddress(int address)
  {
    this.adressen.add(address);
  }
}

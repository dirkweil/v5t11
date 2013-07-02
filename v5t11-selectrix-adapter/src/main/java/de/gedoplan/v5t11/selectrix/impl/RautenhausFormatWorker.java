package de.gedoplan.v5t11.selectrix.impl;

import de.gedoplan.v5t11.selectrix.SelectrixConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class RautenhausFormatWorker extends SelectrixWorker
{
  private static final int REINIT_THRESHOLD = 60000 / Selectrix.PORT_RECEIVE_TIMEOUT_MILLIS;

  private int              initCounter      = 0;

  public RautenhausFormatWorker(InputStream in, OutputStream out)
  {
    super(in, out);
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Thread#run()
   */
  @Override
  public void run()
  {
    try
    {
      while (!isStopRequested())
      {
        if (--this.initCounter <= 0)
        {
          this.initCounter = REINIT_THRESHOLD;

          if (this.logger.isDebugEnabled())
          {
            this.logger.debug("Activate Rautenhaus format");
          }

          /**
           * Rautenhaus-Befehlsformat aktivieren.
           * 
           * Hierdurch werden Änderungen im Selectrix-System automatisch, ohne weitere Anforderung, gemeldet.
           */
          write(126, 0b10010000);
        }

        int adresse = this.in.read();
        if (adresse >= 0)
        {
          this.initCounter = REINIT_THRESHOLD;

          int wert = this.in.read();
          if (wert >= 0)
          {
            if (this.logger.isTraceEnabled())
            {
              this.logger.trace("read: adresse=" + adresse + ", wert=" + wert);
            }

            if (this.adressen.contains(adresse))
            {
              Selectrix.getInstance().setValue(adresse, wert, false);
            }
          }
        }
      }
    }
    catch (IOException e)
    {
      this.logger.error(e);

      // TODO: Abbruch melden
    }
  }

  /**
   * Wert lesen.
   * 
   * @param adresse Adresse
   * @throws IOException bei Fehlern
   * @return wert
   */
  @Override
  protected int read(int adresse) throws IOException
  {
    assert adresse >= 0 && adresse <= SelectrixConnection.MAX_ADDRESSE : "Ungueltige Adresse: " + adresse;

    synchronized (this.inOutLock)
    {
      byte[] buf = { (byte) adresse, (byte) 0 };
      this.out.write(buf);
      this.out.flush();

      // TODO: Warten, bis Wert gelesen
    }

    return Selectrix.getInstance().getValue(adresse, false);
  }
}

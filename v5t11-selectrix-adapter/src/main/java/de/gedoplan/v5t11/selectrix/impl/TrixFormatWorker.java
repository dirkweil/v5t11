package de.gedoplan.v5t11.selectrix.impl;

import de.gedoplan.v5t11.selectrix.SelectrixConnection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TrixFormatWorker extends SelectrixWorker {
  private static final int LOOP_INTERVAL_MILLIS = 250;

  public TrixFormatWorker(InputStream in, OutputStream out) {
    super(in, out);
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Thread#run()
   */
  @Override
  public void run() {
    try {
      while (!isStopRequested()) {
        long start = System.currentTimeMillis();

        for (int adresse : this.adressen) {
          read(adresse);
        }

        long diffMillis = System.currentTimeMillis() - start;
        long waitMillis = LOOP_INTERVAL_MILLIS - diffMillis;
        if (waitMillis > 0) {
          try {
            sleep(waitMillis);
          } catch (InterruptedException e) {
            // ignore
          }
        }

      }
    } catch (IOException e) {
      this.logger.error(e);

      // TODO Q&D: Selectrix neu starten
      new Thread(() -> {
        Selectrix.getInstance().stop();
        Selectrix.getInstance().restart();
      }).start();
    }
  }

  /**
   * Wert lesen.
   *
   * @param adresse
   *          Adresse
   * @throws IOException
   *           bei Fehlern
   * @return wert
   */
  @Override
  protected int read(int adresse) throws IOException {
    assert adresse >= 0 && adresse <= SelectrixConnection.MAX_ADDRESSE : "Ungueltige Adresse: " + adresse;

    synchronized (this.inOutLock) {
      byte[] buf = { (byte) adresse, (byte) 0 };
      this.out.write(buf);
      this.out.flush();

      int wert = this.in.read();
      if (wert >= 0) {
        if (this.logger.isTraceEnabled()) {
          this.logger.trace("read: adresse=" + adresse + ", wert=" + wert);
        }

        Selectrix.getInstance().setValue(adresse, wert, false);
      }
      return wert;
    }
  }
}

package de.gedoplan.v5t11.status.entity.baustein.besetztmelder;

import de.gedoplan.v5t11.status.entity.baustein.Besetztmelder;
import de.gedoplan.v5t11.status.entity.baustein.Konfigurierbar;

import java.util.Timer;
import java.util.TimerTask;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Besetztmelder des Typs SX-BM-1.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@Konfigurierbar
public class SXBM1 extends Besetztmelder {
  private static final int DROPOUT_DELAY = 200;

  protected SXBM1() {
    super(1);
  }

  private Timer dropOutTimer = new Timer();

  private volatile int nextKanalWert;

  private static final Log log = LogFactory.getLog(SXBM1.class);

  @Override
  public void adjustWert(int adr, int kanalWert) {

    if (kanalWert == 0) {
      this.nextKanalWert = 0;
      this.dropOutTimer.schedule(new DropoutTimerTask(), DROPOUT_DELAY);
      if (log.isTraceEnabled()) {
        log.trace(String.format("SXBM1@%d: Start DropOutTimer"));
      }
    } else {
      this.nextKanalWert = kanalWert;
      this.dropOutTimer.cancel();
      adjustWert();
    }
  }

  private void adjustWert() {
    long oldWert = this.wert;

    super.adjustWert(this.adresse, this.nextKanalWert);

    if (log.isTraceEnabled()) {
      log.trace(String.format("SXBM1@%d: %s -> %s", this.adresse, toBinary(oldWert), toBinary(this.wert)));
    }
  }

  private class DropoutTimerTask extends TimerTask {

    @Override
    public void run() {
      if (log.isTraceEnabled()) {
        log.trace(String.format("SXBM1@%d: DropOutTimer fired"));
      }
      adjustWert();
    }

  }

  private static CharSequence toBinary(long value) {
    StringBuilder sb = new StringBuilder();
    for (long i = 31, mask = 1L << 31; i >= 0; i--, mask >>>= 1) {
      sb.append(((value & mask) != 0) ? '1' : '0');
      if (i > 0 && (i % 4) == 0) {
        sb.append('_');
      }
    }

    return sb;
  }
}

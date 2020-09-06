package de.gedoplan.v5t11.status.entity.baustein.besetztmelder;

import de.gedoplan.v5t11.status.entity.baustein.Besetztmelder;
import de.gedoplan.v5t11.status.entity.baustein.Konfigurierbar;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

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
  protected SXBM1() {
    super(1);
  }

  private ConcurrentMap<Integer, Log> logs = new ConcurrentHashMap<>();

  @Override
  public void adjustWert(int adr, int kanalWert) {
    Log log = this.logs.computeIfAbsent(this.adresse, i -> LogFactory.getLog(SXBM1.class.getName() + "@" + i));
    long oldWert = this.wert;

    super.adjustWert(adr, kanalWert);

    if (log.isTraceEnabled()) {
      log.trace(String.format("SXBM1@%04d: %s -> %s", toBinary(oldWert), toBinary(this.wert)));
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

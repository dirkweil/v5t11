package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import de.gedoplan.v5t11.status.entity.fahrweg.Geraet;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Signale.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public abstract class Signal extends Geraet {
  /**
   * Signalstellung.
   *
   * @author dw
   */
  public static enum Stellung {
    /**
     * Halt (Hp0, Hp00, Sr0, Vr0).
     */
    HALT,

    /**
     * Fahrt (Hp1, Vr1).
     */
    FAHRT,

    /**
     * Langsamfahrt (Hp2, Vr2).
     */
    LANGSAMFAHRT,

    /**
     * Rangierfahrt (Sr1).
     */
    RANGIERFAHRT,

    /**
     * Dunkel (Vorsignal am Hauptsignalmast bei Hp0).
     */
    DUNKEL

  };

  /**
   * Map Stellung -> Stellungswert für alle erlaubten Stellungen.
   */
  protected SortedMap<Stellung, Long> stellung2wert = new TreeMap<Stellung, Long>();

  /**
   * Map Stellungswert -> Stellung für alle erlaubten Stellungen.
   */
  protected Map<Long, Stellung> wert2stellung = new HashMap<Long, Stellung>();

  /**
   * Aktuelle Signalstellung.
   */
  @Getter
  protected Stellung stellung = Stellung.HALT;

  /**
   * Ist dies ein Blocksignal?
   */
  @Getter
  protected boolean block;

  /**
   * Konstruktor.
   *
   * @param bitCount
   *          Anzahl genutzter Bits
   */
  protected Signal(int bitCount) {
    super(bitCount);
  }

  /**
   * Erlaubte Stellung hinzufügen.
   *
   * @param stellung
   *          Stellung
   * @param stellungswert
   *          Stellungswert
   */
  protected void addErlaubteStellung(Stellung stellung, long stellungswert) {
    this.stellung2wert.put(stellung, stellungswert);
    this.wert2stellung.put(stellungswert, stellung);
  }

  public Set<Stellung> getErlaubteStellungen() {
    return this.stellung2wert.keySet();
  }

  /**
   * Wert setzen: {@link #stellung}.
   *
   * @param stellung
   *          Wert
   */
  public void setStellung(Stellung stellung) {
    if (!this.stellung2wert.containsKey(stellung)) {
      throw new IllegalArgumentException("Ungueltige Signalstellung)");
    }
    this.stellung = stellung;
  }

  /**
   * Stellungswert für Stellung ermitteln.
   *
   * @param stellung
   *          Stellung
   * @return Stellungswert
   */
  public long getWertForStellung(Stellung stellung) {
    Long wert = this.stellung2wert.get(stellung);
    return wert != null ? wert : 0;
  }

  /**
   * Stellung für Stellungswert ermitteln.
   *
   * @param stellungsWert
   *          Stellungswert
   * @return Stellung
   */
  public Stellung getStellungForWert(long stellungsWert) {
    return this.wert2stellung.get(stellungsWert);
  }

}

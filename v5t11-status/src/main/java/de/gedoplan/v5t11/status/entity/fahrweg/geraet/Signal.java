package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import de.gedoplan.v5t11.status.entity.fahrweg.Geraet;
import de.gedoplan.v5t11.status.util.EventFirer;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.json.bind.annotation.JsonbTransient;
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
  @JsonbTransient
  protected SortedMap<Stellung, Long> stellung2wert = new TreeMap<Stellung, Long>();

  /**
   * Map Stellungswert -> Stellung für alle erlaubten Stellungen.
   */
  @JsonbTransient
  protected Map<Long, Stellung> wert2stellung = new HashMap<Long, Stellung>();

  /**
   * Aktuelle Signalstellung.
   */
  @Getter
  protected Stellung stellung = Stellung.HALT;

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

  @JsonbTransient
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
    setStellung(stellung, true);
  }

  protected void setStellung(Stellung stellung, boolean updateInterface) {
    if (this.stellung != stellung) {
      if (!this.stellung2wert.containsKey(stellung)) {
        throw new IllegalArgumentException("Ungueltige Signalstellung)");
      }

      this.stellung = stellung;

      if (updateInterface) {
        long fdWert = this.funktionsdecoder.getWert();
        fdWert &= (~this.bitMaskeAnschluss);
        fdWert |= getWertForStellung(stellung) << this.anschluss;
        this.funktionsdecoder.setWert(fdWert);
      }

      EventFirer.fire(this);
    }
  }

  /**
   * Stellungswert für Stellung ermitteln.
   *
   * @param stellung
   *          Stellung
   * @return Stellungswert
   */
  @JsonbTransient
  public long getWertForStellung(Stellung stellung) {
    Long wert = this.stellung2wert.get(stellung);
    return wert != null ? wert : 0;
  }

  /**
   * Stellung für Stellungswert ermitteln.
   *
   * @param stellungsWert
   *          Stellungswert
   * @return Stellung oder null, wenn ungueltiger Stellungswert
   */
  @JsonbTransient
  public Stellung getStellungForWert(long stellungsWert) {
    return this.wert2stellung.get(stellungsWert);
  }

  @Override
  public void adjustStatus() {
    Stellung stellungForWert = getStellungForWert((this.funktionsdecoder.getWert() & this.bitMaskeAnschluss) >>> this.anschluss);
    if (stellungForWert != null) {
      setStellung(stellungForWert, false);
    }
  }

}

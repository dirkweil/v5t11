package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import de.gedoplan.v5t11.status.entity.fahrweg.Geraet;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.domain.SignalStellung;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

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
   * Map Stellung -> Stellungswert für alle erlaubten Stellungen.
   */
  @JsonbTransient
  protected SortedMap<SignalStellung, Long> stellung2wert = new TreeMap<SignalStellung, Long>();

  /**
   * Map Stellungswert -> Stellung für alle erlaubten Stellungen.
   */
  @JsonbTransient
  protected Map<Long, SignalStellung> wert2stellung = new HashMap<Long, SignalStellung>();

  /**
   * Aktuelle Signalstellung.
   */
  @Getter(onMethod = @__(@JsonbInclude))
  protected SignalStellung stellung = SignalStellung.HALT;

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
  protected void addErlaubteStellung(SignalStellung stellung, long stellungswert) {
    this.stellung2wert.put(stellung, stellungswert);
    this.wert2stellung.put(stellungswert, stellung);
  }

  @JsonbInclude(full = true)
  public Set<SignalStellung> getErlaubteStellungen() {
    return this.stellung2wert.keySet();
  }

  @JsonbInclude(full = true)
  public String getTyp() {
    return getClass().getSimpleName();
  }

  /**
   * Wert setzen: {@link #stellung}.
   *
   * @param stellung
   *          Wert
   */
  public void setStellung(SignalStellung stellung) {
    setStellung(stellung, true);
  }

  protected void setStellung(SignalStellung stellung, boolean updateInterface) {
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
  public long getWertForStellung(SignalStellung stellung) {
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
  public SignalStellung getStellungForWert(long stellungsWert) {
    return this.wert2stellung.get(stellungsWert);
  }

  @Override
  public void adjustStatus() {
    SignalStellung stellungForWert = getStellungForWert((this.funktionsdecoder.getWert() & this.bitMaskeAnschluss) >>> this.anschluss);
    if (stellungForWert != null) {
      setStellung(stellungForWert, false);
    }
  }

}

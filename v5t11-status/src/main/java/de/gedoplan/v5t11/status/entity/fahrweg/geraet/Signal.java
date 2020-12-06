package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import de.gedoplan.v5t11.status.entity.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.SignalTyp;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractSignal;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Signale.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public abstract class Signal extends AbstractSignal implements FunktionsdecoderGeraet {

  @Getter
  private FunktionsdecoderZuordnung funktionsdecoderZuordnung;

  /**
   * Map Stellung -> Stellungswert für alle erlaubten Stellungen.
   */
  protected SortedMap<SignalStellung, Long> stellung2wert = new TreeMap<SignalStellung, Long>();

  /**
   * Map Stellungswert -> Stellung für alle erlaubten Stellungen.
   */
  protected Map<Long, SignalStellung> wert2stellung = new HashMap<Long, SignalStellung>();

  /**
   * Konstruktor.
   *
   * @param bitCount
   *        Anzahl genutzter Bits
   */
  protected Signal(int bitCount) {
    this.funktionsdecoderZuordnung = new FunktionsdecoderZuordnung(bitCount);
  }

  /**
   * Erlaubte Stellung hinzufügen.
   *
   * @param stellung
   *        Stellung
   * @param stellungswert
   *        Stellungswert
   */
  protected void addErlaubteStellung(SignalStellung stellung, long stellungswert) {
    this.stellung2wert.put(stellung, stellungswert);
    this.wert2stellung.put(stellungswert, stellung);
  }

  @JsonbInclude
  public abstract SignalTyp getTyp();

  /**
   * Wert setzen: {@link #stellung}.
   *
   * @param stellung
   *        Wert
   */
  @Override
  public void setStellung(SignalStellung stellung) {
    setStellung(stellung, true);
  }

  protected void setStellung(SignalStellung stellung, boolean updateInterface) {
    if (getStellung() != stellung) {
      if (!this.stellung2wert.containsKey(stellung)) {
        throw new IllegalArgumentException("Ungueltige Signalstellung)");
      }

      this.lastChangeMillis = System.currentTimeMillis();
      this.stellung = stellung;

      if (updateInterface) {
        long fdWert = this.funktionsdecoderZuordnung.getFunktionsdecoder().getWert();
        fdWert &= (~this.funktionsdecoderZuordnung.getBitMaskeAnschluss());
        fdWert |= getWertForStellung(stellung) << this.funktionsdecoderZuordnung.getAnschluss();
        this.funktionsdecoderZuordnung.getFunktionsdecoder().setWert(fdWert);
      }

      this.eventFirer.fire(this);
    }
  }

  /**
   * Stellungswert für Stellung ermitteln.
   *
   * @param stellung
   *        Stellung
   * @return Stellungswert
   */
  public long getWertForStellung(SignalStellung stellung) {
    Long wert = this.stellung2wert.get(stellung);
    return wert != null ? wert : 0;
  }

  /**
   * Stellung für Stellungswert ermitteln.
   *
   * @param stellungsWert
   *        Stellungswert
   * @return Stellung oder null, wenn ungueltiger Stellungswert
   */
  public SignalStellung getStellungForWert(long stellungsWert) {
    return this.wert2stellung.get(stellungsWert);
  }

  @Override
  public void adjustStatus() {
    SignalStellung stellungForWert = getStellungForWert(
        (this.funktionsdecoderZuordnung.getFunktionsdecoder().getWert() & this.funktionsdecoderZuordnung.getBitMaskeAnschluss()) >>> this.funktionsdecoderZuordnung.getAnschluss());
    if (stellungForWert != null) {
      setStellung(stellungForWert, false);
    }
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName()
        + "{"
        + getBereich()
        + "/"
        + getName()
        + " @ "
        + this.funktionsdecoderZuordnung
        + "}";
  }

  /**
   * Bei JAXB-Unmarshal Attribut idx als anschluss in die Funktionsdecoder-Zuordnung speichern.
   *
   * @param idx
   *        Anschlussnummer
   */
  @XmlAttribute
  public void setIdx(int idx) {
    this.funktionsdecoderZuordnung.setAnschluss(idx);
  }

  /**
   * Nach JAXB-Unmarshal Funktionsdecoder in die Funktionsdecoder-Zuordnung speichern.
   *
   * @param unmarshaller
   *        Unmarshaller
   * @param parent
   *        Parent
   */
  @SuppressWarnings("unused")
  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    if (parent instanceof Funktionsdecoder) {
      this.funktionsdecoderZuordnung.setFunktionsdecoder((Funktionsdecoder) parent);
    } else {
      throw new IllegalArgumentException("Illegal parent " + parent);
    }

  }

}

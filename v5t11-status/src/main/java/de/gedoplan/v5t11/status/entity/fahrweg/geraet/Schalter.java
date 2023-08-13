/*
 * Created on 22.12.2005 by dw
 */
package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import de.gedoplan.v5t11.status.entity.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.domain.attribute.SchalterStellung;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.AbstractGeraet;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

import lombok.Getter;

/**
 * Schalter.
 * <p>
 * Für allgemeine Zwecke, z. B. Umpolung von Kehrschleifenabschnitten.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Schalter extends AbstractGeraet implements FunktionsdecoderGeraet {

  /**
   * Aktuelle Stellung.
   */
  @Getter(onMethod_ = @JsonbInclude)
  private SchalterStellung stellung = SchalterStellung.AUS;

  @Getter
  private FunktionsdecoderZuordnung funktionsdecoderZuordnung;

  /**
   * Konstruktor.
   */
  protected Schalter() {
    this.funktionsdecoderZuordnung = new FunktionsdecoderZuordnung(1);
  }

  /**
   * Wert setzen: {@link #stellung}.
   *
   * @param stellung
   *        Wert
   */
  public void setStellung(SchalterStellung stellung) {
    setStellung(stellung, true);
  }

  protected void setStellung(SchalterStellung stellung, boolean updateInterface) {
    if (this.stellung != stellung) {
      this.lastChangeMillis = System.currentTimeMillis();
      this.stellung = stellung;

      if (updateInterface) {
        long fdWert = this.funktionsdecoderZuordnung.getFunktionsdecoder().getWert();
        fdWert &= (~this.funktionsdecoderZuordnung.getBitMaskeAnschluss());
        fdWert |= getWertForStellung(stellung) << this.funktionsdecoderZuordnung.getAnschluss();
        this.funktionsdecoderZuordnung.getFunktionsdecoder().setWert(fdWert);
      }

      this.eventFirer.fire(this, Changed.Literal.INSTANCE);
    }
  }

  /**
   * Stellungswert für Stellung ermitteln.
   *
   * @param stellung
   *        Stellung
   * @return Stellungswert
   */
  public long getWertForStellung(SchalterStellung stellung) {
    return stellung.ordinal();
  }

  /**
   * Stellung für Stellungswert ermitteln.
   *
   * @param stellungsWert
   *        Stellungswert
   * @return Stellung
   */
  public SchalterStellung getStellungForWert(long stellungsWert) {
    int wert = (int) stellungsWert;
    return SchalterStellung.values()[wert];
  }

  @Override
  public void adjustStatus() {
    setStellung(
      getStellungForWert((this.funktionsdecoderZuordnung.getFunktionsdecoder().getWert() & this.funktionsdecoderZuordnung.getBitMaskeAnschluss()) >>> this.funktionsdecoderZuordnung.getAnschluss()),
      false);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(getClass().getSimpleName());
    sb.append('{');
    sb.append(getId().toString());
    sb.append('[');
    sb.append(this.funktionsdecoderZuordnung);
    sb.append(']');
    sb.append(' ');
    sb.append(getStellung());
    sb.append('}');
    return sb.toString();
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

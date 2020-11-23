/*
 * Created on 22.12.2005 by dw
 */
package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import de.gedoplan.v5t11.status.entity.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractWeiche;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;

/**
 * Weiche.
 *
 * @author dw
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Weiche extends AbstractWeiche implements FunktionsdecoderGeraet {

  @Getter
  private FunktionsdecoderZuordnung funktionsdecoderZuordnung;

  @XmlAttribute
  private boolean invertiert;

  /**
   * Konstruktor.
   */
  protected Weiche() {
    this.funktionsdecoderZuordnung = new FunktionsdecoderZuordnung(1);
  }

  /**
   * Wert setzen: {@link #stellung}.
   *
   * @param stellung
   *        Wert
   */
  @Override
  public void setStellung(WeichenStellung stellung) {
    setStellung(stellung, true);
  }

  protected void setStellung(WeichenStellung stellung, boolean updateInterface) {
    if (getStellung() != stellung) {
      this.lastChangeMillis = System.currentTimeMillis();
      super.setStellung(stellung);

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
  public long getWertForStellung(WeichenStellung stellung) {
    int wert = stellung.ordinal();
    if (this.invertiert) {
      wert = 1 - wert;
    }
    return wert;
  }

  /**
   * Stellung für Stellungswert ermitteln.
   *
   * @param stellungsWert
   *        Stellungswert
   * @return Stellung
   */
  public WeichenStellung getStellungForWert(long stellungsWert) {
    int wert = (int) stellungsWert;
    if (this.invertiert) {
      wert = 1 - wert;
    }
    return WeichenStellung.values()[wert];
  }

  @Override
  @JsonbInclude(full = true)
  public String getGleisabschnittName() {
    boolean doppelweiche = Character.isAlphabetic(getName().charAt(getName().length() - 1));
    if (doppelweiche) {
      return "W" + getName().substring(0, getName().length() - 1);
    } else {
      return "W" + getName();
    }
  }

  @Override
  public void adjustStatus() {
    setStellung(
        getStellungForWert((this.funktionsdecoderZuordnung.getFunktionsdecoder().getWert() & this.funktionsdecoderZuordnung.getBitMaskeAnschluss()) >>> this.funktionsdecoderZuordnung.getAnschluss()),
        false);
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

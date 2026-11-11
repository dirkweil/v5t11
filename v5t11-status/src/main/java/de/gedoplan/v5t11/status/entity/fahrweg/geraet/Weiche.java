/*
 * Created on 22.12.2005 by dw
 */
package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import de.gedoplan.v5t11.status.entity.baustein.Funktionsdecoder;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractWeiche;
import de.gedoplan.v5t11.util.misc.Delay;
import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import lombok.Getter;

/**
 * Weiche.
 *
 * @author dw
 */
@Entity
@Table(name = Weiche.TABLE_NAME)
@XmlAccessorType(XmlAccessType.NONE)
public class Weiche extends AbstractWeiche implements FunktionsdecoderGeraet {

  public static final String TABLE_NAME = "ST_WEICHE";

  @Getter(onMethod_ = @JsonbTransient)
  @Transient
  private FunktionsdecoderZuordnung funktionsdecoderZuordnung;

  @XmlAttribute
  @Transient
  private boolean invertiert;

  /**
   * Ist die Stellung gesichert, d.h. stimmt die Stellung der Weiche mit der Software-Stellung überein?
   * <p>
   * Bei Magnetartikeldecodern ist das nicht sicher, da die Weiche manuell verstellt worden sein könnte, solange die Steuerung
   * ausgeschaltet war. Sie werden daher beim ersten Stellen "gesichert" (erst andere Stellung, dann Sollstellung).
   * Servodecoder erzwingen ihre Stellung und sind stets gesichert.
   */
  @Getter(onMethod_ = @JsonbTransient)
  @Transient
  private volatile boolean stellungGesichert;

  /**
   * Konstruktor.
   */
  protected Weiche() {
    this.funktionsdecoderZuordnung = new FunktionsdecoderZuordnung(1);
  }

  /**
   * Wert setzen: Stellung.
   *
   * @param stellung Wert
   */
  @Override
  public void setStellung(WeichenStellung stellung) {
    if (!this.stellungGesichert) {
      synchronized (this) {
        if (!this.stellungGesichert) {
          // Erst andere Stellung, kurz warten, dann Sollstellung, damit die Weiche sicher geschaltet wird
          setStellung(stellung.getAndereStellung(), true, true);
          if (this.funktionsdecoderZuordnung.getFunktionsdecoder().isEchtbetrieb()) {
            Delay.delay(250);
          }
          setStellung(stellung, true, true);
          this.stellungGesichert = true;
          return;
        }
      }
    }

    setStellung(stellung, true);
  }

  /**
   * Stellungssicherheit zurücksetzen, falls der Decoder die Stellung nicht erzwingt.
   * <p>
   * Wird bei Verbindungsauf-/abbau aufgerufen, da die Weiche dann manuell verstellt worden sein könnte.
   */
  public void resetStellungGesichert() {
    this.stellungGesichert = this.funktionsdecoderZuordnung.getFunktionsdecoder().isStellungsSicher();
  }

  protected void setStellung(WeichenStellung stellung, boolean updateInterface) {
    setStellung(stellung, updateInterface, false);
  }

  private void setStellung(WeichenStellung stellung, boolean updateInterface, boolean force) {
    if (force || getStellung() != stellung) {
      this.lastChangeMillis = System.currentTimeMillis();
      super.setStellung(stellung);

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
   * @param stellung Stellung
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
   * @param stellungsWert Stellungswert
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
  public String getGleisName() {
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
   * @param idx Anschlussnummer
   */
  @XmlAttribute
  public void setIdx(int idx) {
    this.funktionsdecoderZuordnung.setAnschluss(idx);
  }

  /**
   * Nach JAXB-Unmarshal Funktionsdecoder in die Funktionsdecoder-Zuordnung speichern.
   *
   * @param unmarshaller Unmarshaller
   * @param parent Parent
   */
  @SuppressWarnings("unused")
  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    if (parent instanceof Funktionsdecoder funktionsdecoder) {
      this.funktionsdecoderZuordnung.setFunktionsdecoder(funktionsdecoder);
      this.stellungGesichert = funktionsdecoder.isStellungsSicher();
    } else {
      throw new IllegalArgumentException("Illegal parent " + parent);
    }

  }
}

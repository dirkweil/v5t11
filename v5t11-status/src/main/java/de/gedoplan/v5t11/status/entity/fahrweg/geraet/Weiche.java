/*
 * Created on 22.12.2005 by dw
 */
package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import de.gedoplan.baselibs.utils.exception.BugException;
import de.gedoplan.v5t11.status.entity.fahrweg.Geraet;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.domain.WeichenStellung;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Getter;

/**
 * Weiche.
 *
 * @author dw
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Weiche extends Geraet {

  /**
   * Aktuelle Stellung der Weiche.
   */
  @Getter(onMethod = @__(@JsonbInclude))
  private WeichenStellung stellung = WeichenStellung.GERADE;

  /**
   * Konstruktor.
   */
  protected Weiche() {
    super(1);
  }

  /**
   * Wert setzen: {@link #stellung}.
   *
   * @param stellung
   *          Wert
   */
  public void setStellung(WeichenStellung stellung) {
    setStellung(stellung, true);
  }

  protected void setStellung(WeichenStellung stellung, boolean updateInterface) {
    if (this.stellung != stellung) {
      this.stellung = stellung;

      if (updateInterface) {
        long fdWert = this.funktionsdecoder.getWert();
        fdWert &= (~this.bitMaskeAnschluss);
        fdWert |= getWertForStellung(this.stellung) << this.anschluss;
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
  public static long getWertForStellung(WeichenStellung stellung) {
    switch (stellung) {
    case GERADE:
      return 0;

    case ABZWEIGEND:
      return 1;
    }

    throw new BugException("Unbekannte Weichenstellung: " + stellung);
  }

  /**
   * Stellung für Stellungswert ermitteln.
   *
   * @param stellungsWert
   *          Stellungswert
   * @return Stellung
   */
  public static WeichenStellung getStellungForWert(long stellungsWert) {
    return stellungsWert == 0 ? WeichenStellung.GERADE : WeichenStellung.ABZWEIGEND;
  }

  @JsonbInclude(full = true)
  public String getGleisabschnittName() {
    boolean doppelweiche = Character.isAlphabetic(this.name.charAt(this.name.length() - 1));
    if (doppelweiche) {
      return "W" + this.name.substring(0, this.name.length() - 1);
    } else {
      return "W" + this.name;
    }
  }

  @Override
  public void adjustStatus() {
    setStellung(getStellungForWert((this.funktionsdecoder.getWert() & this.bitMaskeAnschluss) >>> this.anschluss), false);
  }
}

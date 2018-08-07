/*
 * Created on 22.12.2005 by dw
 */
package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import de.gedoplan.v5t11.status.entity.fahrweg.Geraet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Getter;
import lombok.Setter;

/**
 * Weiche.
 *
 * @author dw
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Weiche extends Geraet {
  /**
   * Weichenstellung.
   *
   * @author dw
   */
  public static enum Stellung {
    /**
     * gerade.
     */
    GERADE(0),

    /**
     * abzweigend.
     */
    ABZWEIGEND(1);

    @Getter
    private final long stellungsWert;

    private Stellung(int stellungsWert) {
      this.stellungsWert = stellungsWert;
    }

    /**
     * Stellung zu numerischem Wert erstellen.
     *
     * @param stellungsWert
     *          numerischer Wert
     * @return zugehörige Stellung
     */
    public static Stellung getInstance(long stellungsWert) {
      return stellungsWert == 0 ? GERADE : ABZWEIGEND;
    }
  }

  /**
   * Aktuelle Stellung der Weiche.
   */
  @Getter
  @Setter
  private Stellung stellung = Stellung.GERADE;

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
  public void setStellung(Stellung stellung) {
    setStellung(stellung, true);
  }

  protected void setStellung(Stellung stellung, boolean updateInterface) {
    if (this.stellung != stellung) {
      this.stellung = stellung;

      if (updateInterface) {
        long fdWert = this.funktionsdecoder.getWert();
        fdWert &= (~this.bitMaskeAnschluss);
        fdWert |= this.stellung.getStellungsWert() << this.anschluss;
        this.funktionsdecoder.setWert(fdWert);
      }

      publishStatus();
    }
  }

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
    setStellung(Stellung.getInstance((this.funktionsdecoder.getWert() & this.bitMaskeAnschluss) >>> this.anschluss), false);
  }
}

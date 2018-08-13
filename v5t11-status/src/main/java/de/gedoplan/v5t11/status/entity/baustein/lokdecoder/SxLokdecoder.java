package de.gedoplan.v5t11.status.entity.baustein.lokdecoder;

import de.gedoplan.v5t11.status.entity.baustein.Lokdecoder;

import lombok.Getter;

/**
 * Basisklasse für Selectrix-Lokdecoder.
 *
 * @author dw
 */
public abstract class SxLokdecoder extends Lokdecoder {

  /**
   * Bitmaske für Horn im Wert.
   */
  public static final int MASK_HORN = 0x80;

  /**
   * Bitmaske für Licht im Wert.
   */
  public static final int MASK_LICHT = 0x40;

  /**
   * Bitmaske für die Richtung im Wert.
   */
  public static final int MASK_RICHTUNG = 0x20;

  /**
   * Bitmaske für die Geschwindigkeit im Wert.
   */
  public static final int MASK_GESCHWINDIGKEIT = 0x1F;

  /**
   * Maximalwert für die Geschwindigkeit.
   */
  public static final int MAX_GESCHWINDIGKEIT = 31;

  @Getter
  private boolean horn;

  @Getter
  private boolean licht;

  @Getter
  private boolean rueckwaerts;

  @Getter
  private int geschwindigkeit;

  @Override
  public void setHorn(boolean horn) {
    if (this.horn != horn) {
      if (horn) {
        setWert(this.wert | MASK_HORN);
      } else {
        setWert(this.wert & ~MASK_HORN);
      }
    }
  }

  @Override
  public void setLicht(boolean licht) {
    if (this.licht != licht) {
      if (licht) {
        setWert(this.wert | MASK_LICHT);
      } else {
        setWert(this.wert & ~MASK_LICHT);
      }
    }
  }

  @Override
  public void setRueckwaerts(boolean rueckwaerts) {
    if (this.rueckwaerts != rueckwaerts) {
      if (rueckwaerts) {
        setWert(this.wert | MASK_RICHTUNG);
      } else {
        setWert(this.wert & ~MASK_RICHTUNG);
      }
    }
  }

  @Override
  public void setGeschwindigkeit(int geschwindigkeit) {
    if (this.geschwindigkeit != geschwindigkeit) {
      if (geschwindigkeit < 0 || geschwindigkeit > MAX_GESCHWINDIGKEIT) {
        throw new IllegalArgumentException("Ungueltige Geschwindigkeit");
      }

      setWert((this.wert & ~MASK_GESCHWINDIGKEIT) | (geschwindigkeit & MASK_GESCHWINDIGKEIT));
    }
  }

  protected SxLokdecoder(int byteAnzahl) {
    super(byteAnzahl);
  }

  @Override
  public Class<?> getProgrammierfamilie() {
    return SxLokdecoder.class;
  }

  @Override
  public void adjustStatus() {
    this.horn = (this.wert & MASK_HORN) != 0;
    this.licht = (this.wert & MASK_LICHT) != 0;
    this.rueckwaerts = (this.wert & MASK_RICHTUNG) != 0;
    this.geschwindigkeit = (int) (this.wert & MASK_GESCHWINDIGKEIT);

    this.lok.adjustStatus();
  }

}

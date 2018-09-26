package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import de.gedoplan.v5t11.status.entity.baustein.Funktionsdecoder;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Getter
public class FunktionsdecoderZuordnung {
  /**
   * Funktionsdecoder, der das Gerät steuert.
   */
  @Setter(value = AccessLevel.PACKAGE)
  private Funktionsdecoder funktionsdecoder;

  /**
   * Anschluss am Funktionsdecoder (0, 1, ...).
   */
  private int anschluss;

  /**
   * Anzahl benutzter Bits.
   */
  private int bitCount;

  /**
   * Bitmaske für den Wert am Index 0.
   *
   * In diesem Wert sind die niederwertigsten bitCount Bits gesetzt. Er kann somit zum Ausschneiden des Wertes des Geräts aus dem
   * Wert des Funktionsdekoders verwendet werden, wenn dieser zunächst um anschluss Bits nach rechts verschoben wurde.
   */
  private long bitMaske0;

  /**
   * Bitmaske für den Wert am Index anschluss.
   *
   * In diesem Wert sind bitCount Bits passend zum Anschluss gesetzt. Er kann somit zum Ausschneiden des Wertes des Geräts aus dem
   * Wert des Funktionsdekoders verwendet werden.
   */
  private long bitMaskeAnschluss;

  /**
   * Konstruktor.
   *
   * @param bitCount
   *          Anzahl belegter Bits
   */
  FunktionsdecoderZuordnung(int bitCount) {
    this.bitCount = bitCount;
    this.bitMaske0 = (~((-1L) << this.bitCount));
  }

  public void setAnschluss(int anschluss) {
    this.anschluss = anschluss;
    this.bitMaskeAnschluss = this.bitMaske0 << this.anschluss;
  }

  @Override
  public String toString() {
    return this.funktionsdecoder.getAdressen().isEmpty()
        ? "none"
        : this.funktionsdecoder.getAdressen().get(0)
            + "/"
            + this.anschluss;
  }

}

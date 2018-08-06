package de.gedoplan.v5t11.status.entity.fahrweg;

import de.gedoplan.v5t11.status.entity.baustein.Funktionsdecoder;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Basisklasse für Geräte (die an Funktionsdecoder angeschlossen werden).
 *
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class Geraet extends Fahrwegelement {
  /**
   * Funktionsdecoder, der das Gerät steuert.
   */
  protected Funktionsdecoder funktionsdecoder;

  /**
   * Anschluss am Funktionsdecoder (0, 1, ...).
   */
  @XmlAttribute(name = "idx")
  protected int anschluss;

  /**
   * Anzahl benutzter Bits.
   */
  protected int bitCount;

  /**
   * Bitmaske für den Wert am Index 0.
   *
   * In diesem Wert sind die niederwertigsten bitCount Bits gesetzt. Er kann somit zum Ausschneiden des Wertes des Geräts aus dem
   * Wert des Funktionsdekoders verwendet werden, wenn dieser zunächst um anschluss Bits nach rechts verschoben wurde.
   */
  protected long bitMaske0;

  /**
   * Bitmaske für den Wert am Index anschluss.
   *
   * In diesem Wert sind bitCount Bits passend zum Anschluss gesetzt. Er kann somit zum Ausschneiden des Wertes des Geräts aus dem
   * Wert des Funktionsdekoders verwendet werden.
   */
  protected long bitMaskeAnschluss;

  /**
   * Konstruktor.
   *
   * @param bitCount
   *          Anzahl belegter Bits
   */
  protected Geraet(int bitCount) {
    this.bitCount = bitCount;
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.getClass().getSimpleName() + "{" + this.bereich + "/" + this.name + " @ " + this.funktionsdecoder.getAdressen().get(0) + "/" + this.anschluss + "}";
  }

  /**
   * Nachbearbeitung nach JAXB-Unmarshal.
   *
   * @param unmarshaller
   *          Unmarshaller
   * @param parent
   *          Parent
   */
  @SuppressWarnings("unused")
  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    if (parent instanceof Funktionsdecoder) {
      this.funktionsdecoder = (Funktionsdecoder) parent;
    } else {
      throw new IllegalArgumentException("Illegal parent " + parent);
    }

    this.bitMaske0 = (~((-1L) << this.bitCount));
    this.bitMaskeAnschluss = this.bitMaske0 << this.anschluss;

  }

  public abstract void adjustStatus();
}

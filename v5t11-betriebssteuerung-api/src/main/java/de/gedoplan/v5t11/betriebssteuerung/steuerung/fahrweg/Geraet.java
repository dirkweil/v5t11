package de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg;

import de.gedoplan.baselibs.utils.exception.BugException;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Funktionsdecoder;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Basisklasse für Geräte (die an Funktionsdecoder angeschlossen werden).
 * 
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@JsonAutoDetect(JsonMethod.NONE)
public abstract class Geraet extends Fahrwegelement
{
  /**
   * Funktionsdecoder, der das Gerät steuert.
   */
  protected Funktionsdecoder funktionsdecoder;

  /**
   * Anschluss am Funktionsdecoder (0, 1, ...).
   */
  @XmlAttribute(name = "idx")
  @JsonProperty
  protected int              anschluss;

  /**
   * Anzahl benutzter Bits.
   */
  protected int              bitCount;

  /**
   * Hat der Funktiondecoder-Ausgang Dauerstrom?
   */
  @XmlAttribute(name = "dauer")
  @JsonProperty
  private boolean            dauer;

  /**
   * Bitmaske für den Wert am Index 0.
   * 
   * In diesem Wert sind die niederwertigsten bitCount Bits gesetzt. Er kann somit zum Ausschneiden des Wertes des Geräts aus dem
   * Wert des Funktionsdekoders verwendet werden, wenn dieser zunächst um anschluss Bits nach rechts verschoben wurde.
   */
  protected long             bitMaske0;

  /**
   * Bitmaske für den Wert am Index anschluss.
   * 
   * In diesem Wert sind bitCount Bits passend zum Anschluss gesetzt. Er kann somit zum Ausschneiden des Wertes des Geräts aus dem
   * Wert des Funktionsdekoders verwendet werden.
   */
  protected long             bitMaskeAnschluss;

  /**
   * Konstruktor.
   * 
   * @param bitCount Anzahl belegter Bits
   */
  protected Geraet(int bitCount)
  {
    this.bitCount = bitCount;
  }

  /**
   * Attribut liefern: {@link #funktionsdecoder}.
   * 
   * @return Wert
   */
  public Funktionsdecoder getFunktionsdecoder()
  {
    return this.funktionsdecoder;
  }

  /**
   * Wert setzen: {@link #funktionsdecoder}.
   * 
   * @param funktionsdecoder Wert
   */
  public void setFunktionsdecoder(Funktionsdecoder funktionsdecoder)
  {
    this.funktionsdecoder = funktionsdecoder;
  }

  /**
   * Attribut liefern: {@link #anschluss}.
   * 
   * @return Wert
   */
  public int getAnschluss()
  {
    return this.anschluss;
  }

  /**
   * Attribut liefern: {@link #bitCount}.
   * 
   * @return Wert
   */
  public int getBitCount()
  {
    return this.bitCount;
  }

  /**
   * Attribut liefern: {@link #dauer}.
   * 
   * @return Wert
   */
  public boolean isDauer()
  {
    return this.dauer;
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return this.getClass().getSimpleName() + "{" + this.bereich + "/" + this.name + " @ " + this.funktionsdecoder.getAdresse() + "/" + this.anschluss + "}";
  }

  /**
   * Nachbearbeitung nach JAXB-Unmarshal.
   * 
   * @param unmarshaller Unmarshaller
   * @param parent Parent
   */
  @SuppressWarnings("unused")
  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent)
  {
    if (parent instanceof Funktionsdecoder)
    {
      this.funktionsdecoder = (Funktionsdecoder) parent;
    }
    else
    {
      throw new IllegalArgumentException("Illegal parent " + parent);
    }

    this.bitMaske0 = (~((-1L) << this.bitCount));
    this.bitMaskeAnschluss = this.bitMaske0 << this.anschluss;

  }

  /**
   * Konstruktor für JAXB.
   * 
   * Dieser Konstrktor ist für JAXB nötig, sollte aber dennoch nie aufgerufen werden.
   */
  protected Geraet()
  {
    throw new BugException("Sollte nie aufgerufen werden");
  }
}

package de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein;

import de.gedoplan.v5t11.selectrix.SelectrixException;

import java.util.Arrays;
import java.util.function.Supplier;

import javax.enterprise.inject.Typed;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Selectrix-Zentrale.
 *
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@JsonAutoDetect(JsonMethod.NONE)
@Typed
public class Zentrale extends Baustein {
  // Adressen: ............................................ [--127--]_[--109--]_[--106--]_[--105--]_[--104--]
  private static final long MASK_AKTIV /* .......... */ = 0b1000_0000_0000_0000_0000_0000_0000_0000_0000_0000L;
  private static final long MASK_KURZSCHLUSS /* .... */ = 0b0000_0000_0001_0000_0000_0000_0000_0000_0000_0000L;
  private static final long MASK_BEREIT /* ......... */ = 0b0000_0000_0010_0000_0000_0000_0000_0000_0000_0000L;
  private static final long MASK_CMD_PROG /* ....... */ = 0b0000_0000_0000_0000_0100_0000_0000_0000_0000_0000L;
  private static final long MASK_CMD_LESEN /* ...... */ = 0b0000_0000_0000_0000_1000_0001_0000_0000_0000_0000L;
  private static final long MASK_CMD_SCHREIBEN /* .. */ = 0b0000_0000_0000_0000_1000_1001_0000_0000_0000_0000L;
  private static final long MASK_DECODER /* ........ */ = 0b0000_0000_0000_0000_0000_0000_1111_1111_1111_1111L;

  /**
   * Konstruktor.
   */
  public Zentrale() {
    super(5);
    this.id = "Zentrale";
    this.adresse = 104;
    this.adressCache.set(Arrays.asList(104, 105, 106, 109, 127));
  }

  /**
   * Ist die Zentrale aktiv (Gleisspannung an)?
   *
   * @return <code>true</code>, wenn Zentrale aktiv
   */
  @XmlElement
  @JsonProperty
  public boolean isAktiv() {
    return (getWert() & MASK_AKTIV) != 0;
  }

  /**
   * Zentrale ein/ausschalten.
   *
   * @param aktiv
   *          <code>true</code> zum Einschalten
   */
  public void setAktiv(boolean aktiv) {
    long wert = getWert() & ~MASK_AKTIV;
    if (aktiv) {
      wert |= MASK_AKTIV;
    }
    setWert(wert);
  }

  /**
   * Kurzschluss?
   *
   * @return <code>true</code> bei Kurzschluss
   */
  @XmlElement
  @JsonProperty
  public boolean isKurzschluss() {
    return (getWert() & MASK_KURZSCHLUSS) != 0;
  }

  @Override
  public String getLabel() {
    return getLabelPrefix();
  }

  @Override
  public String getLabelPrefix() {
    return "Zentrale";
  }

  public int readDecoderDaten() {
    setAktiv(false);

    startProgMode();

    try {
      requestDecoderDaten();

      return (int) (getWert() & MASK_DECODER);
    } finally {
      stopProgMode();
    }
  }

  private void startProgMode() {
    setWert(getWert() | MASK_CMD_PROG);
    waitFor(() -> (getWert() & MASK_BEREIT) != 0, 2000, "Kann Programmiermodus nicht anfordern");
  }

  private void stopProgMode() {
    setWert(getWert() & ~MASK_CMD_PROG);
  }

  private void requestDecoderDaten() {
    setWert(getWert() | MASK_CMD_LESEN);
    waitFor(() -> (getWert() & MASK_BEREIT) != 0, 2000, "Kann Decoderdaten nicht lesen");
  }

  private void storeDecoderDaten() {
    setWert(getWert() | MASK_CMD_SCHREIBEN);
    waitFor(() -> (getWert() & MASK_BEREIT) != 0, 2000, "Kann Decoderdaten nicht schreiben");
  }

  private static void waitFor(Supplier<Boolean> condition, long millis, String errorMessage) {
    for (int i = 0; i < 10; ++i) {
      if (condition.get()) {
        return;
      }

      delay(millis / 10);
    }

    throw new SelectrixException(errorMessage);
  }

  private static void delay(long millis) {
    try {
      Thread.currentThread().wait(millis);
    } catch (InterruptedException e) {
    }

  }

  public void writeDecoderDaten(int decoderDaten) {
    setAktiv(false);

    startProgMode();

    try {
      setWert((getWert() & ~MASK_DECODER) | (decoderDaten & MASK_DECODER));
      storeDecoderDaten();
    } finally {
      stopProgMode();
    }
  }
}

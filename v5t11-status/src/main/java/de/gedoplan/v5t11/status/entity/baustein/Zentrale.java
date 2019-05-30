package de.gedoplan.v5t11.status.entity.baustein;

import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;

/**
 * Selectrix-Zentrale.
 *
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Zentrale extends Baustein {
  // Adressen: ............................................ [--127--]_[--109--]_[--106--]_[--105--]_[--104--]
  private static final long MASK_GLEISSPANNUNG /* .. */ = 0b1000_0000_0000_0000_0000_0000_0000_0000_0000_0000L;
  private static final long MASK_PROG_AKTIV /* ..... */ = 0b0000_0000_0100_0000_0000_0000_0000_0000_0000_0000L;
  private static final long MASK_ZE_BEREIT /* ...... */ = 0b0000_0000_0010_0000_0000_0000_0000_0000_0000_0000L;
  private static final long MASK_KURZSCHLUSS /* .... */ = 0b0000_0000_0001_0000_0000_0000_0000_0000_0000_0000L;
  private static final long MASK_PROG_AUSF /* ...... */ = 0b0000_0000_0000_0000_1000_0000_0000_0000_0000_0000L;
  private static final long MASK_PROG_ANF /* ....... */ = 0b0000_0000_0000_0000_0100_0000_0000_0000_0000_0000L;
  private static final long MASK_PROG_SCHREIBEN /* . */ = 0b0000_0000_0000_0000_0000_1000_0000_0000_0000_0000L;

  @Getter(onMethod_ = @JsonbInclude)
  private boolean gleisspannung;

  @Getter(onMethod_ = @JsonbInclude)
  private boolean progAktiv;

  @Getter(onMethod_ = @JsonbInclude)
  private boolean zentraleBereit;

  @Getter(onMethod_ = @JsonbInclude)
  private boolean kurzschluss;

  @Getter(onMethod_ = @JsonbInclude)
  private boolean progAusfuehren;

  @Getter(onMethod_ = @JsonbInclude)
  private boolean progAnfordern;

  @Getter(onMethod_ = @JsonbInclude)
  private boolean progSchreiben;

  /**
   * Konstruktor.
   */
  public Zentrale() {
    super(5);
    this.id = "Zentrale";
    this.adresse = 104;
    this.adressen.set(Arrays.asList(104, 105, 106, 109, 127));
  }

  /**
   * Gleisspannung ein/ausschalten.
   *
   * @param gleisspannung
   *          <code>true</code> zum Einschalten
   */
  public void setGleisspannung(boolean gleisspannung) {
    if (this.gleisspannung != gleisspannung) {
      long wert = getWert() & ~MASK_GLEISSPANNUNG;
      if (gleisspannung) {
        wert |= MASK_GLEISSPANNUNG;
      }
      setWert(wert);

      EventFirer.getInstance().fire(this);
    }
  }

  @Override
  public String getLabel() {
    return getLabelPrefix();
  }

  @Override
  public String getLabelPrefix() {
    return "Zentrale";
  }

  @Override
  public void adjustStatus() {
    this.gleisspannung = (getWert() & MASK_GLEISSPANNUNG) != 0;
    this.progAktiv = (getWert() & MASK_PROG_AKTIV) != 0;
    this.zentraleBereit = (getWert() & MASK_ZE_BEREIT) != 0;
    this.kurzschluss = (getWert() & MASK_KURZSCHLUSS) != 0;
    this.progAusfuehren = (getWert() & MASK_PROG_AUSF) != 0;
    this.progAnfordern = (getWert() & MASK_PROG_ANF) != 0;
    this.progSchreiben = (getWert() & MASK_PROG_SCHREIBEN) != 0;

    EventFirer.getInstance().fire(this);
  }
}

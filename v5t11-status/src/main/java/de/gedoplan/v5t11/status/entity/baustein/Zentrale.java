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
  private static final long MASK_AKTIV /* .......... */ = 0b1000_0000_0000_0000_0000_0000_0000_0000_0000_0000L;
  private static final long MASK_KURZSCHLUSS /* .... */ = 0b0000_0000_0001_0000_0000_0000_0000_0000_0000_0000L;

  @Getter(onMethod_ = @JsonbInclude)
  private boolean aktiv;

  @Getter(onMethod_ = @JsonbInclude)
  private boolean kurzschluss;

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
   * Zentrale ein/ausschalten.
   *
   * @param aktiv
   *          <code>true</code> zum Einschalten
   */
  public void setAktiv(boolean aktiv) {
    if (this.aktiv != aktiv) {
      long wert = getWert() & ~MASK_AKTIV;
      if (aktiv) {
        wert |= MASK_AKTIV;
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
    this.aktiv = (getWert() & MASK_AKTIV) != 0;
    this.kurzschluss = (getWert() & MASK_KURZSCHLUSS) != 0;
  }
}

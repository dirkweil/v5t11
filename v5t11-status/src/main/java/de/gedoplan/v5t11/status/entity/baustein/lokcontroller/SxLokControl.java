package de.gedoplan.v5t11.status.entity.baustein.lokcontroller;

import de.gedoplan.v5t11.status.entity.baustein.Lokcontroller;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.cdi.EventFirer;

import java.util.Objects;

import jakarta.inject.Inject;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * Lokcontroller.
 *
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class SxLokControl extends Lokcontroller {

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
   * Bitmaske für die Fahrstufe im Wert.
   */
  public static final int MASK_FAHRSTUFE = 0x1F;

  /**
   * Maximalwert für die Fahrstufe.
   */
  public static final int MAX_FAHRSTUFE = 31;

  private int hornBits;

  private long invertMask;

  private double fahrstufenFaktor;

  // SxLokControl
  @Inject
  EventFirer eventFirer;

  public SxLokControl() {
    super(1);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setLok(Fahrzeug lok, int hornBits) {
    if (!Objects.equals(lok, this.lok)) {

      // Falls bisher zugeordnete Lok steht, inaktiv setzen
      if (this.lok != null && this.lok.getFahrstufe() == 0) {
        this.lok.setAktiv(false);
      }

      this.lok = lok;

      // Falls nun neue Lok zugeordnet, ...
      if (this.lok != null) {
        this.hornBits = hornBits;

        // Falls Licht oder Richtung von Controller und Lok nicht übereinstimmen, zugehöriges Bit in invertMask merken
        this.invertMask = 0;
        if (this.lok.isLicht() != ((this.wert & MASK_LICHT) != 0)) {
          this.invertMask |= MASK_LICHT;
        }
        if (this.lok.isRueckwaerts() != ((this.wert & MASK_RICHTUNG) != 0)) {
          this.invertMask |= MASK_RICHTUNG;
        }

        // Fahrstufen-Umrechnungsfaktor errechnen
        this.fahrstufenFaktor = (double) this.lok.getId().getSystemTyp().getMaxFahrstufe() / MAX_FAHRSTUFE;

        // Lok aktiv setzen
        this.lok.setAktiv(true);
      }

      this.eventFirer.fire(this, Changed.Literal.INSTANCE);
    }
  }

  @Override
  public String getLabelPrefix() {
    return "Lokcontroller";
  }

  @Override
  public void adjustStatus() {
    if (this.lok != null) {
      long thisWert = this.wert ^ this.invertMask;
      boolean licht = (thisWert & MASK_LICHT) != 0;
      boolean rueckwaerts = (thisWert & MASK_RICHTUNG) != 0;
      int fahrstufe = (int) ((this.wert & MASK_FAHRSTUFE) * this.fahrstufenFaktor);

      this.lok.setLicht(licht);
      this.lok.setRueckwaerts(rueckwaerts);
      this.lok.setFahrstufe(fahrstufe);

      if (this.hornBits != 0) {
        int fktBits = this.lok.getFktBits();
        if ((thisWert & MASK_HORN) != 0) {
          fktBits |= this.hornBits;
        } else {
          fktBits &= (~this.hornBits);
        }
        this.lok.setFktBits(fktBits);
      }
    }
  }
}

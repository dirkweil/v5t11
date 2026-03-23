package de.gedoplan.v5t11.status.entity.baustein.lokcontroller;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Lokcontroller;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeugdecoder;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.cdi.EventFirer;

import java.util.Objects;

import org.jboss.logging.Logger;

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

  @Inject
  Steuerung steuerung;

  @Inject
  Logger logger;


  public SxLokControl() {
    super(1);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void setFahrzeugdecoder(Fahrzeugdecoder fzd, int hornBits) {

    this.logger.debugf("SxLokControl@%d: %s", this.getAdresse(), fzd);

    if (!Objects.equals(fzd, this.fahrzeugdecoder)) {

      // Falls bisher zugeordneter Decoder Fahrstufe 0 hat, inaktiv setzen
      if (this.fahrzeugdecoder != null && this.fahrzeugdecoder.getFahrstufe() == 0) {
        this.fahrzeugdecoder.setAktiv(false);
      }

      this.fahrzeugdecoder = fzd;

      // Falls nun neuer Decoder zugeordnet, ...
      if (this.fahrzeugdecoder != null) {
        this.hornBits = hornBits;

        // Falls Licht oder Richtung von Controller und Lok nicht übereinstimmen, zugehöriges Bit in invertMask merken
        this.invertMask = 0;
        if (this.fahrzeugdecoder.isLicht() != ((this.wert & MASK_LICHT) != 0)) {
          this.invertMask |= MASK_LICHT;
        }
        if (this.fahrzeugdecoder.isRueckwaerts() != ((this.wert & MASK_RICHTUNG) != 0)) {
          this.invertMask |= MASK_RICHTUNG;
        }

        // Fahrstufen-Umrechnungsfaktor errechnen
        this.fahrstufenFaktor = (double) this.fahrzeugdecoder.getId().getSystemTyp().getMaxFahrstufe() / MAX_FAHRSTUFE;

        // Decoder aktiv setzen
        this.fahrzeugdecoder.setAktiv(true);
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
    // Hack: Mit Horn-Taste Gleisspannung einschalten (OMG)
    if (!this.steuerung.getZentrale().isGleisspannung() && (this.wert & MASK_HORN) != 0) {
      this.steuerung.getZentrale().setGleisspannung(true);
    }

    if (this.fahrzeugdecoder != null) {
      // Q&D: Fahrzeugdecode neu von Steuerung holen (wg. Disconnect/Connect)
      this.fahrzeugdecoder = this.steuerung.getFahrzeugdecoder(this.fahrzeugdecoder.getId());

      long thisWert = this.wert ^ this.invertMask;
      boolean licht = (thisWert & MASK_LICHT) != 0;
      boolean rueckwaerts = (thisWert & MASK_RICHTUNG) != 0;
      int fahrstufe = (int) ((this.wert & MASK_FAHRSTUFE) * this.fahrstufenFaktor);

      this.fahrzeugdecoder.setLicht(licht);
      this.fahrzeugdecoder.setRueckwaerts(rueckwaerts);
      this.fahrzeugdecoder.setFahrstufe(fahrstufe);

      if (this.hornBits != 0) {
        int fktBits = this.fahrzeugdecoder.getFktBits();
        if ((thisWert & MASK_HORN) != 0) {
          fktBits |= this.hornBits;
        } else {
          fktBits &= (~this.hornBits);
        }
        this.fahrzeugdecoder.setFktBits(fktBits);
      }
    }

    this.logger.debugf("SxLokControl@%d: %s", this.getAdresse(), this.fahrzeugdecoder);

  }
}

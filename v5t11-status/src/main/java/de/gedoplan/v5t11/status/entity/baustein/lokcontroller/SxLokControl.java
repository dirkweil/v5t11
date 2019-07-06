package de.gedoplan.v5t11.status.entity.baustein.lokcontroller;

import de.gedoplan.v5t11.status.entity.baustein.Lokcontroller;
import de.gedoplan.v5t11.status.entity.lok.Lok;
import de.gedoplan.v5t11.util.cdi.EventFirer;

import java.util.Objects;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

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

  private long invertMask;

  private double fahrstufenFaktor;

  @Inject
  EventFirer eventFirer;

  public SxLokControl() {
    super(1);
  }

  /**
   * Wert setzen: {@link #lok}.
   *
   * @param lok
   *        Wert
   */
  @Override
  public void setLok(Lok lok) {
    if (!Objects.equals(lok, this.lok)) {

      if (lok != null) {
        this.invertMask = 0;

        this.fahrstufenFaktor = (double) lok.getMaxFahrstufe() / MAX_FAHRSTUFE;
      }

      this.lok = lok;

      this.eventFirer.fire(this);
    }
  }

  @Override
  public String getLabelPrefix() {
    return "Lokcontroller";
  }

  @Override
  public void adjustStatus() {
    if (this.lok != null) {
      boolean horn = (this.wert & MASK_HORN) != 0;
      boolean licht = (this.wert & MASK_LICHT) != 0;
      boolean rueckwaerts = (this.wert & MASK_RICHTUNG) != 0;
      int fahrstufe = (int) ((this.wert & MASK_FAHRSTUFE) * this.fahrstufenFaktor);

      System.out.printf("%s: horn=%b, licht=%b, rueckwaerts=%b, fahrstufe=%d\n", this, horn, licht, rueckwaerts, fahrstufe);
      this.lok.setLicht(licht);
      this.lok.setRueckwaerts(rueckwaerts);
      this.lok.setFahrstufe(fahrstufe);
    }
  }
}

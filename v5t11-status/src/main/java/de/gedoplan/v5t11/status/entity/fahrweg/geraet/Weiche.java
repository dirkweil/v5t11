/*
 * Created on 22.12.2005 by dw
 */
package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import de.gedoplan.v5t11.status.entity.fahrweg.Geraet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Getter;
import lombok.Setter;

/**
 * Weiche.
 *
 * @author dw
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Weiche extends Geraet {
  /**
   * Weichenstellung.
   *
   * @author dw
   */
  public static enum Stellung {
    /**
     * gerade.
     */
    GERADE(0),

    /**
     * abzweigend.
     */
    ABZWEIGEND(1);

    @Getter
    private final int value;

    private Stellung(int value) {
      this.value = value;
    }

    /**
     * Stellung zu numerischem Wert erstellen.
     *
     * @param stellungsWert
     *          numerischer Wert
     * @return zugehörige Stellung
     */
    public static Stellung getInstance(long stellungsWert) {
      return stellungsWert == 0 ? GERADE : ABZWEIGEND;
    }
  }

  /**
   * Aktuelle Stellung der Weiche.
   */
  @Getter
  @Setter
  private Stellung stellung = Stellung.GERADE;

  /**
   * Konstruktor.
   */
  protected Weiche() {
    super(1);
  }

  public String getGleisabschnittName() {
    boolean doppelweiche = Character.isAlphabetic(this.name.charAt(this.name.length() - 1));
    if (doppelweiche) {
      return "W" + this.name.substring(0, this.name.length() - 1);
    } else {
      return "W" + this.name;
    }
  }
}

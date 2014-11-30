/*
 * Created on 22.12.2005 by dw
 */
package de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet;

import de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedEvent;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Geraet;
import de.gedoplan.v5t11.betriebssteuerung.util.IconUtil;

import javax.swing.Icon;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Weiche.
 *
 * @author dw
 *
 */
@XmlAccessorType(XmlAccessType.NONE)
public class Weiche extends Geraet
{
  /**
   * Weichenstellung.
   *
   * @author dw
   */
  public static enum Stellung
  {
    /**
     * gerade.
     */
    GERADE(0)
    {
      @Override
      public Icon getIcon()
      {
        return IconUtil.getIcon("images/weiche_gerade.gif");
      }
    },

    /**
     * abzweigend.
     */
    ABZWEIGEND(1)
    {
      @Override
      public Icon getIcon()
      {
        return IconUtil.getIcon("images/weiche_abzweigend.gif");
      }
    };

    private final int value;

    private Stellung(int value)
    {
      this.value = value;
    }

    /**
     * Numerischen Wert der Stellung liefern.
     *
     * @return Wert
     */
    public int getValue()
    {
      return this.value;
    }

    /**
     * Stellung zu numerischem Wert erstellen.
     *
     * @param stellungsWert numerischer Wert
     * @return zugehörige Stellung
     */
    public static Stellung getInstance(long stellungsWert)
    {
      return stellungsWert == 0 ? GERADE : ABZWEIGEND;
    }

    /**
     * Icon zur Stellung liefern.
     *
     * @return Icon
     */
    public abstract Icon getIcon();
  }

  /**
   * Aktuelle Stellung der Weiche.
   */
  private Stellung stellung = Stellung.GERADE;

  /**
   * Wenn <code>null</code>, ist die Weiche nicht Teil einer vorgeschlagenen Fahrstrasse. Sonst ist hier die gewünschte Stellung
   * eingetragen.
   */
  private Stellung stellungFuerFahrstrassenvorschlag;

  /**
   * Attribut liefern: {@link #stellung}.
   *
   * @return Wert
   */
  public Stellung getStellung()
  {
    return this.stellung;
  }

  /**
   * Wert setzen: {@link #stellung}.
   *
   * @param stellung Wert
   */
  public void setStellung(Stellung stellung)
  {
    setStellung(stellung, true);
  }

  /**
   * Wert setzen: {@link #stellung}.
   *
   * @param stellung Wert
   * @param updateInterface wenn <code>true</code>, veränderten Wert an Interface schicken
   */
  public void setStellung(Stellung stellung, boolean updateInterface)
  {
    if (!stellung.equals(this.stellung))
    {
      this.stellung = stellung;

      if (updateInterface)
      {
        long fdWert = this.funktionsdecoder.getWert();
        fdWert &= (~this.bitMaskeAnschluss);
        fdWert |= this.stellung.getValue() << this.anschluss;
        this.funktionsdecoder.setWert(fdWert);
      }

      if (this.valueChangedListenerRegistry != null)
      {
        this.valueChangedListenerRegistry.sendEvent(new ValueChangedEvent(this));
      }
    }
  }

  @Override
  public void valueChanged(ValueChangedEvent event)
  {
    long stellungsWert = (this.funktionsdecoder.getWert() >>> this.anschluss) & this.bitMaske0;
    setStellung(Stellung.getInstance(stellungsWert), false);
  }

  /**
   * Konstruktor.
   */
  protected Weiche()
  {
    super(1);
  }

  /**
   * Wert liefern: {@link #stellungFuerFahrstrassenvorschlag}.
   *
   * @return Wert
   */
  public Stellung getStellungFuerFahrstrassenvorschlag()
  {
    return this.stellungFuerFahrstrassenvorschlag;
  }

  /**
   * Wert setzen: {@link #stellungFuerFahrstrassenvorschlag}.
   *
   * @param stellung Wert
   */
  public void setStellungFuerFahrstrassenvorschlag(Stellung stellung)
  {
    if (this.stellungFuerFahrstrassenvorschlag != stellung)
    {
      this.stellungFuerFahrstrassenvorschlag = stellung;

      if (this.valueChangedListenerRegistry != null)
      {
        this.valueChangedListenerRegistry.sendEvent(new ValueChangedEvent(this));
      }
    }
  }
}

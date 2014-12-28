package de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet;

import de.gedoplan.baselibs.utils.exception.BugException;
import de.gedoplan.v5t11.betriebssteuerung.event.EventFirer;
import de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedEvent;
import de.gedoplan.v5t11.betriebssteuerung.qualifier.StellungsAenderung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse.ReservierungsTyp;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Geraet;
import de.gedoplan.v5t11.betriebssteuerung.util.IconUtil;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.enterprise.util.AnnotationLiteral;
import javax.swing.Icon;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Signale.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class Signal extends Geraet
{
  /**
   * Signalstellung.
   *
   * @author dw
   */
  public static enum Stellung
  {
    /**
     * Halt (Hp0, Hp00, Sr0, Vr0).
     */
    HALT
    {
      @Override
      public Icon getSelectedIcon()
      {
        return IconUtil.getIcon("images/signal_rot_selektiert.gif");
      }

      @Override
      public Icon getUnselectedIcon()
      {
        return IconUtil.getIcon("images/signal_rot.gif");
      }

    },

    /**
     * Fahrt (Hp1, Vr1).
     */
    FAHRT
    {
      @Override
      public Icon getSelectedIcon()
      {
        return IconUtil.getIcon("images/signal_gruen_selektiert.gif");
      }

      @Override
      public Icon getUnselectedIcon()
      {
        return IconUtil.getIcon("images/signal_gruen.gif");
      }

    },

    /**
     * Langsamfahrt (Hp2, Vr2).
     */
    LANGSAMFAHRT
    {
      @Override
      public Icon getSelectedIcon()
      {
        return IconUtil.getIcon("images/signal_gelb_selektiert.gif");
      }

      @Override
      public Icon getUnselectedIcon()
      {
        return IconUtil.getIcon("images/signal_gelb.gif");
      }

    },

    /**
     * Rangierfahrt (Sr1).
     */
    RANGIERFAHRT
    {
      @Override
      public Icon getSelectedIcon()
      {
        return IconUtil.getIcon("images/signal_weiss_selektiert.gif");
      }

      @Override
      public Icon getUnselectedIcon()
      {
        return IconUtil.getIcon("images/signal_weiss.gif");
      }

    },

    /**
     * Dunkel (Vorsignal am Hauptsignalmast bei Hp0).
     */
    DUNKEL
    {
      @Override
      public Icon getSelectedIcon()
      {
        return IconUtil.getIcon("images/signal_dunkel_selektiert.gif");
      }

      @Override
      public Icon getUnselectedIcon()
      {
        return IconUtil.getIcon("images/signal_dunkel.gif");
      }
    };

    /**
     * Icon für selektiertes Signal liefern.
     *
     * @return Icon
     */
    public abstract Icon getSelectedIcon();

    /**
     * Icon für unselektiertes Signal liefern.
     *
     * @return Icon
     */
    public abstract Icon getUnselectedIcon();
  };

  /**
   * Map Stellung -> Stellungswert für alle erlaubten Stellungen.
   */
  protected SortedMap<Stellung, Long> stellung2wert = new TreeMap<Stellung, Long>();

  /**
   * Map Stellungswert -> Stellung für alle erlaubten Stellungen.
   */
  protected Map<Long, Stellung>       wert2stellung = new HashMap<Long, Stellung>();

  /**
   * Aktuelle Signalstellung.
   */
  protected Stellung                  stellung      = Stellung.HALT;

  /**
   * Ist dies ein Blocksignal?
   */
  protected boolean                   block;

  /**
   * Konstruktor.
   *
   * @param bitCount Anzahl genutzter Bits
   */
  protected Signal(int bitCount)
  {
    super(bitCount);
  }

  /**
   * Erlaubte Stellung hinzufügen.
   *
   * @param stellung Stellung
   * @param stellungswert Stellungswert
   */
  protected void addErlaubteStellung(Stellung stellung, long stellungswert)
  {
    this.stellung2wert.put(stellung, stellungswert);
    this.wert2stellung.put(stellungswert, stellung);
  }

  public Set<Stellung> getErlaubteStellungen()
  {
    return this.stellung2wert.keySet();
  }

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
    setStellung(stellung, null);
  }

  /**
   * Stellung setzen, ggf. angepasst an eine Fahrstrasse.
   *
   * @param stellung Stellung
   * @param reservierungsTyp Fahrstrassentyp oder <code>null</code>
   */
  public void setStellung(Stellung stellung, ReservierungsTyp reservierungsTyp)
  {
    stellung = getAngepassteStellung(stellung, reservierungsTyp);
    setStellung(stellung, true);
  }

  /**
   * Wert setzen: {@link #stellung}.
   *
   * @param stellung Wert
   * @param updateInterface wenn <code>true</code>, veränderten Wert an Interface schicken
   */
  private void setStellung(Stellung stellung, boolean updateInterface)
  {
    if (!stellung.equals(this.stellung))
    {
      this.stellung = stellung;

      if (updateInterface)
      {
        long fdWert = this.funktionsdecoder.getWert();
        fdWert &= (~this.bitMaskeAnschluss);
        fdWert |= getWertForStellung(stellung) << this.anschluss;
        this.funktionsdecoder.setWert(fdWert);
      }

      if (this.valueChangedListenerRegistry != null)
      {
        this.valueChangedListenerRegistry.sendEvent(new ValueChangedEvent(this));
      }

      EventFirer.fireEvent(this, new AnnotationLiteral<StellungsAenderung>()
      {
      });
    }
  }

  /**
   * Stellungswert für Stellung ermitteln.
   *
   * @param stellung Stellung
   * @return Stellungswert
   */
  public long getWertForStellung(Stellung stellung)
  {
    Long wert = this.stellung2wert.get(stellung);
    return wert != null ? wert : 0;
  }

  /**
   * Stellung passend zum Reservierungstyp liefern.
   *
   * @param stellung gewünschte Stellung
   * @param reservierungsTyp Reservierungstyp, falls Signal für eine Fahrstrasse gestellt wird, sonst <code>null</code>
   * @return angepasste Stellung
   */
  private Stellung getAngepassteStellung(Stellung stellung, ReservierungsTyp reservierungsTyp)
  {

    // HALT ist immer OK
    if (stellung == Stellung.HALT)
    {
      return Stellung.HALT;
    }

    Set<Stellung> erlaubteStellungen = getErlaubteStellungen();

    // 1. Szenario: Signal unabhängig von Fahrstrasse stellen:
    if (reservierungsTyp == null)
    {
      // Falls gewünschte Stellung möglich, diese nehmen
      if (erlaubteStellungen.contains(stellung))
      {
        return stellung;
      }

      // Sonst weiter wie für Zugfahrt
    }

    // 2. Szenario: Signal für Rangierfahrt stellen
    if (reservierungsTyp == ReservierungsTyp.RANGIERFAHRT)
    {
      // Erste mögliche Stellung aus RANGIERFAHRT, LANGSAMFAHRT, FAHRT nehmen
      if (erlaubteStellungen.contains(Stellung.RANGIERFAHRT))
      {
        return Stellung.RANGIERFAHRT;
      }
      if (erlaubteStellungen.contains(Stellung.LANGSAMFAHRT))
      {
        return Stellung.LANGSAMFAHRT;
      }
      if (erlaubteStellungen.contains(Stellung.FAHRT))
      {
        return Stellung.FAHRT;
      }
    }

    // 3. Szenario: Signal für Zugfahrt stellen
    if (reservierungsTyp == ReservierungsTyp.ZUGFAHRT)
    {
      switch (stellung)
      {
      case FAHRT:
        // FAHRT gewünscht: Erste mögliche Stellung aus FAHRT, LANGSAMFAHRT, RANGIERFAHRT nehmen
        if (erlaubteStellungen.contains(Stellung.FAHRT))
        {
          return Stellung.FAHRT;
        }
        if (erlaubteStellungen.contains(Stellung.LANGSAMFAHRT))
        {
          return Stellung.LANGSAMFAHRT;
        }
        if (erlaubteStellungen.contains(Stellung.RANGIERFAHRT))
        {
          return Stellung.RANGIERFAHRT;
        }
        break;

      case LANGSAMFAHRT:
        // LANGSAMFAHRT gewünscht: Erste mögliche Stellung aus LANGSAMFAHRT, FAHRT, RANGIERFAHRT nehmen
        if (erlaubteStellungen.contains(Stellung.LANGSAMFAHRT))
        {
          return Stellung.LANGSAMFAHRT;
        }
        if (erlaubteStellungen.contains(Stellung.FAHRT))
        {
          return Stellung.FAHRT;
        }
        if (erlaubteStellungen.contains(Stellung.RANGIERFAHRT))
        {
          return Stellung.RANGIERFAHRT;
        }
        break;

      case RANGIERFAHRT:
        // RANGIERFAHRT gewünscht: Erste mögliche Stellung aus RANGIERFAHRT, LANGSAMFAHRT, FAHRT nehmen
        if (erlaubteStellungen.contains(Stellung.RANGIERFAHRT))
        {
          return Stellung.RANGIERFAHRT;
        }
        if (erlaubteStellungen.contains(Stellung.LANGSAMFAHRT))
        {
          return Stellung.LANGSAMFAHRT;
        }
        if (erlaubteStellungen.contains(Stellung.FAHRT))
        {
          return Stellung.FAHRT;
        }
        break;

      default:
        break;
      }
    }

    // Wenn nix ging: HALT
    return Stellung.HALT;
  }

  @Override
  public void valueChanged(ValueChangedEvent event)
  {
    long stellungsWert = (this.funktionsdecoder.getWert() >>> this.anschluss) & this.bitMaske0;
    setStellung(getStellungForWert(stellungsWert), false);
  }

  /**
   * Stellung für Stellungswert ermitteln.
   *
   * @param stellungsWert Stellungswert
   * @return Stellung
   */
  public Stellung getStellungForWert(long stellungsWert)
  {
    return this.wert2stellung.get(stellungsWert);
  }

  /**
   * Farben für das GBS liefern.
   *
   * @return Farben
   */
  public abstract Color[] getGBSFarben();

  /**
   * Wert liefern: {@link #block}.
   *
   * @return Wert
   */
  public boolean isBlock()
  {
    return this.block;
  }

  /**
   * Wert setzen: {@link #block}.
   *
   * @param block Wert
   */
  public void setBlock(boolean block)
  {
    this.block = block;
  }

  /**
   * Konstruktor für JAXB.
   *
   * Dieser Konstrktor ist für JAXB nötig, sollte aber dennoch nie aufgerufen werden.
   */
  protected Signal()
  {
    throw new BugException("Sollte nie aufgerufen werden");
  }

}

package de.gedoplan.v5t11.betriebssteuerung.steuerung.konfiguration;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.Bereichselement;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Hauptsignal;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Vorsignal;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class VorsignalKonfiguration extends Bereichselement
{
  /**
   * Zugeordnete Hauptsignale.
   */
  @XmlElement(name = "Hauptsignal")
  private Set<Bereichselement> hauptsignale;

  /**
   * Hauptsignal am gleichen Mast oder <code>null</code>, wenn separat stehend.
   */
  @XmlAttribute(name = "mast")
  private String               nameHauptsignalAmGleichenMast;

  /**
   * Wird das Vorsignal automatisch - durch das Hauptsignal am gleichen Mast - dunkel geschaltet?
   */
  @XmlAttribute
  private boolean              autodunkel;

  private Steuerung            steuerung;

  /**
   * Vorsignal liefern.
   * 
   * @return Vorsignal oder <code>null</code>, wenn nicht vorhanden
   */
  public Vorsignal getVorsignal()
  {
    Signal signal = this.steuerung.getSignal(this.bereich, this.name);
    if (signal != null && signal instanceof Vorsignal)
    {
      return (Vorsignal) signal;
    }

    return null;
  }

  /**
   * Hauptsignal am gleichen Mast liefern.
   * 
   * @return Hauptsignal oder <code>null</code>, wenn nicht vorhanden
   */
  public Hauptsignal getHauptsignalAmGleichenMast()
  {
    if (this.nameHauptsignalAmGleichenMast != null)
    {
      Signal signal = this.steuerung.getSignal(this.bereich, this.nameHauptsignalAmGleichenMast);
      if (signal != null && signal instanceof Hauptsignal)
      {
        return (Hauptsignal) signal;
      }
    }

    return null;
  }

  /**
   * Hauptsignale liefern.
   * 
   * @return Hauptsignale
   */
  public Set<Hauptsignal> getHauptsignale()
  {
    Set<Hauptsignal> result = new HashSet<>();
    for (Bereichselement element : this.hauptsignale)
    {
      Signal signal = this.steuerung.getSignal(element.getBereich(), element.getName());
      if (signal != null && signal instanceof Hauptsignal)
      {
        result.add((Hauptsignal) signal);
      }
    }
    return result;
  }

  /**
   * Wert liefern: {@link #autodunkel}.
   * 
   * @return Wert
   */
  public boolean isAutodunkel()
  {
    return this.autodunkel;
  }

  /**
   * Nachbearbeitung nach JAXB-Unmarshal.
   * 
   * @param unmarshaller Unmarshaller
   * @param parent Parent
   */
  public void afterUnmarshal(Unmarshaller unmarshaller, Object parent)
  {
    if (parent instanceof Steuerung)
    {
      this.steuerung = (Steuerung) parent;
    }
    else
    {
      throw new IllegalArgumentException("Illegal parent " + parent);
    }

    for (Bereichselement element : this.hauptsignale)
    {
      if (element.getBereich() == null)
      {
        element.setBereich(this.bereich);
      }
    }
  }
}

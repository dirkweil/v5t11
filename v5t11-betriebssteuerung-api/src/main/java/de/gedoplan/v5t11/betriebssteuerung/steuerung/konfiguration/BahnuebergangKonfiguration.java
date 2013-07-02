package de.gedoplan.v5t11.betriebssteuerung.steuerung.konfiguration;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.Bereichselement;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Bahnuebergang;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Signal;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class BahnuebergangKonfiguration extends Bereichselement
{
  @XmlElement(name = "Gleisabschnitt")
  private Set<Bereichselement> gleisabschnitte;

  private Steuerung            steuerung;

  /**
   * Bahnübergang liefern.
   * 
   * @return Bahnübergang oder <code>null</code>, wenn nicht vorhanden
   */
  public Bahnuebergang getBahnuebergang()
  {
    Signal signal = this.steuerung.getSignal(this.bereich, this.name);
    if (signal != null && signal instanceof Bahnuebergang)
    {
      return (Bahnuebergang) signal;
    }

    return null;
  }

  /**
   * Gleisabschnitte des Bahnübergangs liefern.
   * 
   * @return Gleisabschnitte
   */
  public Set<Gleisabschnitt> getGleisabschnitte()
  {
    Set<Gleisabschnitt> result = new HashSet<>();
    for (Bereichselement element : this.gleisabschnitte)
    {
      Gleisabschnitt gleisabschnitt = this.steuerung.getGleisabschnitt(element.getBereich(), element.getName());
      if (gleisabschnitt != null)
      {
        result.add(gleisabschnitt);
      }
    }
    return result;
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

    for (Bereichselement element : this.gleisabschnitte)
    {
      if (element.getBereich() == null)
      {
        element.setBereich(this.bereich);
      }
    }
  }
}

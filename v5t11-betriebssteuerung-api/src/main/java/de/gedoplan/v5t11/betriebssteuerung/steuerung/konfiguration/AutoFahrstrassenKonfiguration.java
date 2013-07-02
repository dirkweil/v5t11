package de.gedoplan.v5t11.betriebssteuerung.steuerung.konfiguration;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.Bereichselement;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Gleisabschnitt;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class AutoFahrstrassenKonfiguration extends Bereichselement
{
  @XmlElement(name = "Ziel")
  private List<Bereichselement> zielGleisabschnitte;

  private Steuerung             steuerung;

  /**
   * Auslösenden Gleisabschnitt liefern.
   * 
   * @return Gleisabschnitt oder <code>null</code>, wenn nicht vorhanden
   */
  public Gleisabschnitt getGleisabschnitt()
  {
    return this.steuerung.getGleisabschnitt(this.bereich, this.name);
  }

  /**
   * Potenzielle Ziele liefern.
   * 
   * @return Ziel-Gleisabschnitte
   */
  public List<Gleisabschnitt> getZielGleisabschnitte()
  {
    List<Gleisabschnitt> result = new ArrayList<>();
    for (Bereichselement element : this.zielGleisabschnitte)
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

    for (Bereichselement element : this.zielGleisabschnitte)
    {
      if (element.getBereich() == null)
      {
        element.setBereich(this.bereich);
      }
    }
  }
}

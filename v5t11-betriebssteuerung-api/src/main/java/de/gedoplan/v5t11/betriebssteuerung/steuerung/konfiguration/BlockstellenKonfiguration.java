package de.gedoplan.v5t11.betriebssteuerung.steuerung.konfiguration;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.Bereichselement;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Signal;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class BlockstellenKonfiguration extends Bereichselement
{
  @XmlElement(name = "Gleisabschnitt")
  private Bereichselement gleisabschnitt;

  private Steuerung       steuerung;

  /**
   * Signal der Blockstelle liefern.
   * 
   * @return Signal oder <code>null</code>, wenn nicht vorhanden
   */
  public Signal getSignal()
  {
    return this.steuerung.getSignal(this.bereich, this.name);
  }

  /**
   * Gleisabschnitt der Blockstelle liefern.
   * 
   * @return Gleisabschnitt oder <code>null</code>, wenn nicht vorhanden
   */
  public Gleisabschnitt getGleisabschnitt()
  {
    return this.steuerung.getGleisabschnitt(this.gleisabschnitt.getBereich(), this.gleisabschnitt.getName());
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

    if (this.gleisabschnitt.getBereich() == null)
    {
      this.gleisabschnitt.setBereich(this.bereich);
    }
  }
}

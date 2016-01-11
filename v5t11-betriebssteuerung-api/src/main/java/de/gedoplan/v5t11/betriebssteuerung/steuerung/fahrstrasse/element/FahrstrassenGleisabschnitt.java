package de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.element;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.FahrstrassenElement;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Gleisabschnitt;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Fahrstrassenelement 'Gleisabschnitt'.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public class FahrstrassenGleisabschnitt extends FahrstrassenElement
{
  /**
   * Zugeordneter Gleisabschnitt.
   */
  private Gleisabschnitt gleisabschnitt;

  /**
   * Ist dies ein Gleisabschnitt zu einer Weiche?
   */
  private boolean        weichenGleisabschnitt;

  /**
   * Konstruktor.
   *
   * @param gleisabschnitt Gleisabschnitt
   * @param weichenGleisabschnitt <code>true</code>, falls Gleisabschnitt zu einer Weiche
   */
  public FahrstrassenGleisabschnitt(Gleisabschnitt gleisabschnitt, boolean weichenGleisabschnitt)
  {
    this.gleisabschnitt = gleisabschnitt;
    this.weichenGleisabschnitt = weichenGleisabschnitt;

    this.bereich = gleisabschnitt.getBereich();
    this.name = gleisabschnitt.getName();
  }

  /**
   * Wert liefern: {@link #weichenGleisabschnitt}.
   *
   * @return Wert
   */
  public boolean isWeichenGleisabschnitt()
  {
    return this.weichenGleisabschnitt;
  }

  @Override
  public Gleisabschnitt getFahrwegelement()
  {
    return this.gleisabschnitt;
  }

  @Override
  public void setFahrwegelement(Steuerung steuerung)
  {
    this.gleisabschnitt = steuerung.getGleisabschnitt(this.bereich, this.name);
  }

  @Override
  public void reservieren(Fahrstrasse fahrstrasse)
  {
    if (this.gleisabschnitt != null)
    {
      this.gleisabschnitt.reserviereFuerFahrstrasse(fahrstrasse, isZaehlrichtung());
    }
  }

  @Override
  public void vorschlagen(Fahrstrasse fahrstrasse)
  {
    if (this.gleisabschnitt != null)
    {
      this.gleisabschnitt.schlageFuerFahrstrasseVor(fahrstrasse, isZaehlrichtung());
    }
  }

  /**
   * Konstruktor für JAXB.
   */
  protected FahrstrassenGleisabschnitt()
  {
  }

  @Override
  public Rank getRank()
  {
    return Rank.GLEISABSCHNITT;
  }
}

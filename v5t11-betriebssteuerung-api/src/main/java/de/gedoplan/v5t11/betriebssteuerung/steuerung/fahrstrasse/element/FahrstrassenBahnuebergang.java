package de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.element;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.FahrstrassenElement;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Bahnuebergang;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Fahrstrassenelement 'Signal'.
 * 
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public class FahrstrassenBahnuebergang extends FahrstrassenElement
{
  /**
   * Zugeordneter Bahnübergang.
   */
  private Bahnuebergang bahnuebergang;

  @Override
  public Bahnuebergang getFahrwegelement()
  {
    return this.bahnuebergang;
  }

  @Override
  public void setFahrwegelement(Steuerung steuerung)
  {
    this.bahnuebergang = (Bahnuebergang) steuerung.getSignal(this.bereich, this.name);
  }

  /**
   * Konstruktor für interne Zwecke.
   */
  protected FahrstrassenBahnuebergang()
  {
    this.schutz = true;
  }

  @Override
  public void reservieren(Fahrstrasse fahrstrasse)
  {
    if (this.bahnuebergang != null)
    {
      this.bahnuebergang.reserviereFuerFahrstrasse(fahrstrasse, isZaehlrichtung());

      /*
       * Ein Bahnübergang wird durch Fahrstrassen *nicht* gestellt. Dies geschieht ausschliesslich durch die
       * BahnuebergangsSteuerung!
       */
    }
  }

  @Override
  public void vorschlagen(Fahrstrasse fahrstrasse)
  {
    if (this.bahnuebergang != null)
    {
      this.bahnuebergang.schlageFuerFahrstrasseVor(fahrstrasse, isZaehlrichtung());
    }
  }

}

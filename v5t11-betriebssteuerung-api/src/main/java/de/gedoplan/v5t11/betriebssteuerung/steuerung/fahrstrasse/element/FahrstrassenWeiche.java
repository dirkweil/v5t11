package de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.element;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.FahrstrassenElement;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Weiche.Stellung;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Fahrstrassenelement 'Weiche'.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public class FahrstrassenWeiche extends FahrstrassenElement
{
  /**
   * Zugeordnete Weiche.
   */
  private Weiche   weiche;

  /**
   * Gewünschte Stellung der Weiche.
   */
  private Stellung stellung;

  @Override
  public Weiche getFahrwegelement()
  {
    return this.weiche;
  }

  @Override
  public void setFahrwegelement(Steuerung steuerung)
  {
    this.weiche = steuerung.getWeiche(this.bereich, this.name);
  }

  public Stellung getStellung()
  {
    return this.stellung;
  }

  /**
   * Konstruktor für interne Zwecke.
   */
  protected FahrstrassenWeiche()
  {
  }

  @Override
  public void reservieren(Fahrstrasse fahrstrasse)
  {
    if (this.weiche != null)
    {
      if (!this.schutz)
      {
        this.weiche.reserviereFuerFahrstrasse(fahrstrasse, isZaehlrichtung());
      }

      if (fahrstrasse != null)
      {
        this.weiche.setStellung(this.stellung);
      }
    }
  }

  @Override
  public void vorschlagen(Fahrstrasse fahrstrasse)
  {
    if (this.weiche != null)
    {
      if (!this.schutz)
      {
        this.weiche.schlageFuerFahrstrasseVor(fahrstrasse, isZaehlrichtung());
        this.weiche.setStellungFuerFahrstrassenvorschlag(fahrstrasse != null ? this.stellung : null);
      }
    }
  }

  @SuppressWarnings("unused")
  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent)
  {
    this.stellung = Stellung.valueOf(this.stellungsName);
  }

  @Override
  public Rank getRank()
  {
    if (isSchutz())
    {
      return null;
    }

    return this.stellung == Stellung.ABZWEIGEND ? Rank.WEICHE_ABZWEIGEND : Rank.WEICHE_GERADE;
  }
}

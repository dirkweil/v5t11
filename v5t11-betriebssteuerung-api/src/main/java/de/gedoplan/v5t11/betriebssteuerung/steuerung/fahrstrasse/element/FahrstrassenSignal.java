package de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.element;

import de.gedoplan.baselibs.utils.exception.BugException;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse.ReservierungsTyp;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.FahrstrassenElement;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Signal.Stellung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Vorsignal;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;

/**
 * Fahrstrassenelement 'Signal'.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@JsonAutoDetect(JsonMethod.NONE)
public class FahrstrassenSignal extends FahrstrassenElement implements Cloneable
{
  /**
   * Zugeordnetes Signal.
   */
  private Signal   signal;

  /**
   * Gewünschte Stellung des Signals.
   */
  private Stellung stellung;

  @Override
  public Signal getFahrwegelement()
  {
    return this.signal;
  }

  @Override
  public void setFahrwegelement(Steuerung steuerung)
  {
    this.signal = steuerung.getSignal(this.bereich, this.name);
  }

  /**
   * Wert liefern: {@link #stellung}.
   *
   * @return Wert
   */
  public Stellung getStellung()
  {
    return this.stellung;
  }

  public FahrstrassenSignal createCopy(Stellung stellung)
  {
    try
    {
      FahrstrassenSignal copy = (FahrstrassenSignal) clone();
      copy.stellung = stellung;
      copy.stellungsName = stellung.name();
      return copy;
    }
    catch (CloneNotSupportedException e)
    {
      throw new BugException("Cannot clone FahrstrassenSignal");
    }
  }

  public FahrstrassenSignal(Signal signal)
  {
    this.signal = signal;
    this.stellung = Stellung.FAHRT;

    this.bereich = signal.getBereich();
    this.name = signal.getName();
  }

  /**
   * Konstruktor für interne Zwecke.
   */
  protected FahrstrassenSignal()
  {
  }

  @Override
  public void reservieren(Fahrstrasse fahrstrasse)
  {
    if (this.signal != null)
    {
      if (!this.schutz)
      {
        this.signal.reserviereFuerFahrstrasse(fahrstrasse, isZaehlrichtung());
      }

      if (!this.signal.isBlock() && !(this.signal instanceof Vorsignal))
      {
        if (fahrstrasse != null)
        {
          ReservierungsTyp reservierungsTyp = fahrstrasse.getReservierungsTyp();
          this.signal.setStellung(this.stellung, reservierungsTyp);
        }
        else
        {
          this.signal.setStellung(Stellung.HALT);
        }
      }
    }
  }

  @Override
  public void vorschlagen(Fahrstrasse fahrstrasse)
  {
    if (this.signal != null)
    {
      if (!this.schutz)
      {
        this.signal.schlageFuerFahrstrasseVor(fahrstrasse, isZaehlrichtung());
      }
    }
  }

  @SuppressWarnings("unused")
  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent)
  {
    this.stellung = this.stellungsName != null ? Stellung.valueOf(this.stellungsName) : Stellung.FAHRT;

    if (this.stellung.equals(Stellung.HALT))
    {
      this.schutz = true;
    }
  }
}

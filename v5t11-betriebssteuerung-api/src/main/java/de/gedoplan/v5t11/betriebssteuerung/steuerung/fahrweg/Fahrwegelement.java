package de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg;

import de.gedoplan.v5t11.betriebssteuerung.event.EventFirer;
import de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedEvent;
import de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedListener;
import de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedListenerRegistry;
import de.gedoplan.v5t11.betriebssteuerung.qualifier.FahrstrassenZuordnung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Bereichselement;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse;

import javax.enterprise.util.AnnotationLiteral;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Element eines Fahrwegs.
 *
 * Ein solches Element kann ein Gerät sein (Signal, Weiche etc.) oder ein Gleisabschnitt.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public abstract class Fahrwegelement extends Bereichselement implements ValueChangedListener
{
  /**
   * Registry für Wertänderungs-Listener.
   */
  protected transient ValueChangedListenerRegistry valueChangedListenerRegistry;

  /**
   * Falls dieses Element Teil einer reservierten Fahrstrasse ist, diese Fahrstrasse, sonst <code>null</code>
   */
  protected Fahrstrasse                            reserviertefahrstrasse;

  /**
   * Falls dieses Element Teil einer vorgeschlagenen Fahrstrasse ist, diese Fahrstrasse, sonst <code>null</code>
   */
  protected Fahrstrasse                            vorgeschlageneFahrstrasse;

  /**
   * In Zählrichtung orientiert?
   */
  private boolean                                  zaehlrichtung;

  /**
   * Für Fahrstrasse als (un)reserviert markieren.
   *
   * @param fahrstrasse reservierte Fahrstrasse, zu der dieses Element gehört, oder <code>null</code>, wenn die Reservierung
   *        aufgehoben wird
   */
  public void reserviereFuerFahrstrasse(Fahrstrasse fahrstrasse, boolean zaehlrichtung)
  {
    if (this.reserviertefahrstrasse != fahrstrasse)
    {
      this.reserviertefahrstrasse = fahrstrasse;
      this.zaehlrichtung = zaehlrichtung;

      if (this.valueChangedListenerRegistry != null)
      {
        this.valueChangedListenerRegistry.sendEvent(new ValueChangedEvent(this));
      }

      fireFahrstrassenZuordnung();
    }
  }

  /**
   * Wert liefern: {@link #fahrstrassenReservierung}.
   *
   * @return Wert
   */
  public Fahrstrasse getReservierteFahrstrasse()
  {
    return this.reserviertefahrstrasse;
  }

  /**
   * Wert liefern: {@link #fahrstrasseVorgeschlagen}.
   *
   * @return Wert
   */
  public Fahrstrasse getVorgeschlageneneFahrstrasse()
  {
    return this.vorgeschlageneFahrstrasse;
  }

  /**
   * Für Fahrstrasse als vorgeschlagen markieren.
   *
   * @param fahrstrasse vorgeschlagene Fahrstrasse, zu der dieses Element gehört, oder <code>null</code>, wenn der Vorschlag
   *        aufgehoben wird
   */
  public void schlageFuerFahrstrasseVor(Fahrstrasse vorgeschlageneFahrstrasse, boolean zaehlrichtung)
  {
    if (this.vorgeschlageneFahrstrasse != vorgeschlageneFahrstrasse)
    {
      this.vorgeschlageneFahrstrasse = vorgeschlageneFahrstrasse;
      this.zaehlrichtung = zaehlrichtung;

      if (this.valueChangedListenerRegistry != null)
      {
        this.valueChangedListenerRegistry.sendEvent(new ValueChangedEvent(this));
      }
    }
  }

  /**
   * Wert liefern: {@link #zaehlrichtung}.
   *
   * @return Wert
   */
  public boolean isZaehlrichtung()
  {
    return this.zaehlrichtung;
  }

  /**
   * Listener für Wertänderungen hinzufügen.
   *
   * @param valueChangedListener Listener
   * @see de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedListenerRegistry#addListener(de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedListener)
   */
  public void addValueChangedListener(ValueChangedListener valueChangedListener)
  {
    if (this.valueChangedListenerRegistry == null)
    {
      this.valueChangedListenerRegistry = new ValueChangedListenerRegistry();
    }
    this.valueChangedListenerRegistry.addListener(valueChangedListener);
  }

  /**
   * Listener für Wertänderungen entfernen.
   *
   * @param valueChangedListener Listener
   * @see de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedListenerRegistry#addListener(de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedListener)
   */
  public void removeValueChangedListener(ValueChangedListener valueChangedListener)
  {
    if (this.valueChangedListenerRegistry != null)
    {
      this.valueChangedListenerRegistry.removeListener(valueChangedListener);
    }
  }

  private void fireFahrstrassenZuordnung()
  {
    EventFirer.fireEvent(this, new AnnotationLiteral<FahrstrassenZuordnung>()
    {
    });
  }
}

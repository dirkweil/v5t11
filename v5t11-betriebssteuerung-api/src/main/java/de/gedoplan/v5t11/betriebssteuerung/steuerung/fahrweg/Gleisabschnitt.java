package de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg;

import de.gedoplan.v5t11.betriebssteuerung.event.EventFirer;
import de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedEvent;
import de.gedoplan.v5t11.betriebssteuerung.qualifier.GleisBelegung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Besetztmelder;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.element.FahrstrassenGleisabschnitt;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.element.FahrstrassenWeiche;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Weiche;

import javax.enterprise.util.AnnotationLiteral;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Gleisabschnitt.
 *
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@JsonAutoDetect(JsonMethod.NONE)
public class Gleisabschnitt extends Fahrwegelement
{
  /**
   * Besetztmelder, der den Gleisabschnitt überwacht.
   */
  private Besetztmelder besetztmelder;

  /**
   * Anschluss am Besetztmelder (0, 1, ...)
   */
  @XmlAttribute(name = "idx")
  @JsonProperty
  private int           anschluss;

  /**
   * Gleisabschnitt besetzt?
   */
  @JsonProperty
  private boolean       besetzt;

  /**
   * Folge-Gleisabschnitte entgegen der Zählrichtung (Index 0) und in Zählrichtung (Index 1).
   */
  FolgeGleisabschnitt[] folgeGleisabschnitte = new FolgeGleisabschnitt[2];

  /**
   * Attribut liefern: {@link #besetztmelder}.
   *
   * @return Wert
   */
  public Besetztmelder getBesetztmelder()
  {
    return this.besetztmelder;
  }

  /**
   * Wert setzen: {@link #besetztmelder}.
   *
   * @param besetztmelder Wert
   */
  public void setBesetztmelder(Besetztmelder besetztmelder)
  {
    this.besetztmelder = besetztmelder;
  }

  /**
   * Attribut liefern: {@link #anschluss}.
   *
   * @return Wert
   */
  public int getAnschluss()
  {
    return this.anschluss;
  }

  /**
   * Wert liefern: {@link #besetzt}.
   *
   * @return Wert
   */
  public boolean isBesetzt()
  {
    return this.besetzt;
  }

  /**
   * {@inheritDoc}
   *
   * @see de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedListener#valueChanged(de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedEvent)
   */
  @Override
  public void valueChanged(ValueChangedEvent event)
  {
    boolean old = this.besetzt;
    this.besetzt = (this.besetztmelder.getWert() & (1 << this.anschluss)) != 0;
    if (old != this.besetzt)
    {
      if (this.valueChangedListenerRegistry != null)
      {
        this.valueChangedListenerRegistry.sendEvent(new ValueChangedEvent(this));
      }

      fireGleisBelegung();
    }
  }

  private void fireGleisBelegung()
  {
    EventFirer.fireEvent(this, new AnnotationLiteral<GleisBelegung>()
    {});
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return this.getClass().getSimpleName()
        + "{"
        + this.bereich + "/" + this.name
        + " @ " + this.besetztmelder.getAdresse() + "/" + this.anschluss
        + "}"
        + " ["
        + "next=" + this.folgeGleisabschnitte[1]
        + ", prev=" + this.folgeGleisabschnitte[0]
        + "]";
  }

  /**
   * Konstruktor für interne Zwecke.
   */
  protected Gleisabschnitt()
  {
  }

  /**
   * Nachbearbeitung nach JAXB-Unmarshal.
   *
   * @param unmarshaller Unmarshaller
   * @param parent Parent
   */
  @SuppressWarnings("unused")
  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent)
  {
    if (parent instanceof Besetztmelder)
    {
      this.besetztmelder = (Besetztmelder) parent;
    }
    else
    {
      throw new IllegalArgumentException("Illegal parent " + parent);
    }
  }

  public void addFolgeGleisabschnitt(boolean zaehlrichtung, FahrstrassenWeiche fahrstrassenWeiche, FahrstrassenGleisabschnitt nextFahrstrassenGleisabschnitt)
  {
    Gleisabschnitt nextGleisabschnitt = nextFahrstrassenGleisabschnitt.getFahrwegelement();
    Weiche weiche = null;
    Weiche.Stellung stellung = null;
    if (fahrstrassenWeiche != null)
    {
      Weiche tmpWeiche = fahrstrassenWeiche.getFahrwegelement();

      // Weiche nur dann beutzen, wenn der nächste Gleisabschnitt nicht zur Weiche gehört, da der Gleisabschnitt sonst
      // unabhängig von der Weichenstellung erreicht wird
      if (!nextGleisabschnitt.getBereich().equals(tmpWeiche.getBereich())
          || !nextGleisabschnitt.getName().equals(tmpWeiche.getGleisabschnittName()))
      {
        weiche = tmpWeiche;
        stellung = fahrstrassenWeiche.getStellung();
      }
    }

    int idx = zaehlrichtung ? 1 : 0;

    if (this.folgeGleisabschnitte[idx] == null)
    {
      this.folgeGleisabschnitte[idx] = new FolgeGleisabschnitt(weiche, stellung, nextGleisabschnitt);
    }
    else
    {
      this.folgeGleisabschnitte[idx].add(weiche, stellung, nextGleisabschnitt);
    }

  }

}

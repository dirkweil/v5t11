package de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg;

import de.gedoplan.v5t11.betriebssteuerung.event.EventFirer;
import de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedEvent;
import de.gedoplan.v5t11.betriebssteuerung.qualifier.GleisBelegung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein.Besetztmelder;

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
      if (this.logger.isTraceEnabled())
      {
        this.logger.trace(this + ": setBesetzt(" + this.besetzt + ")");
      }

      this.valueChangedListenerRegistry.sendEvent(new ValueChangedEvent(this));

      fireGleisBelegung();
    }
  }

  private void fireGleisBelegung()
  {
    EventFirer.fireEvent(this, new AnnotationLiteral<GleisBelegung>()
    {
    });
  }

  /**
   * {@inheritDoc}
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString()
  {
    return this.getClass().getSimpleName() + "{" + this.bereich + "/" + this.name + " @ " + this.besetztmelder.getAdresse() + "/" + this.anschluss + "}";
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

}

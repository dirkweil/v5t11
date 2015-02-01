package de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Gleisabschnitt;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Besetztmelder.
 *
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@JsonAutoDetect(JsonMethod.NONE)
public abstract class Besetztmelder extends Baustein implements Encoder
{
  /**
   * Zugeordnete Gleisabschnitte.
   */
  @XmlElement(name = "Gleisabschnitt", type = Gleisabschnitt.class)
  @JsonProperty
  protected SortedSet<Gleisabschnitt> gleisabschnitte = new TreeSet<>();

  /**
   * Konstruktor.
   *
   * Wird nur während des JAXB-Unmarshalling aufgerufen.
   *
   * @param byteAnzahl Anzahl belegter Bytes (Adressen)
   */
  protected Besetztmelder(int byteAnzahl)
  {
    super(byteAnzahl);
  }

  /**
   * Konstruktor für JPA.
   *
   * Dieser Konstrktor ist auch für JAXB nötig, sollte hierdurch aber nie aufgerufen werden.
   */
  protected Besetztmelder()
  {
  }

  /**
   * Wert liefern: {@link #gleisabschnitte}.
   *
   * @return Wert
   */
  public SortedSet<Gleisabschnitt> getGleisabschnitte()
  {
    return this.gleisabschnitte;
  }

  /**
   * Alle Gleisabschnitte als ValueChangeListener registrieren.
   */
  public void addValueChangedListeners()
  {
    for (Gleisabschnitt gleisabschnitt : this.gleisabschnitte)
    {
      addValueChangedListener(gleisabschnitt);
    }
  }

  @Override
  public String getLabelPrefix()
  {
    return "Besetzmelder";
  }

}

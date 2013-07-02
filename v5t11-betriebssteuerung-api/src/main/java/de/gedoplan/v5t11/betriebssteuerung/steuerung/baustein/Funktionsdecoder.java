package de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.Geraet;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Bahnuebergang;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.HauptsignalRtGe;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.HauptsignalRtGn;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.HauptsignalRtGnGe;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Hauptsperrsignal;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Sperrsignal;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Vorsignal;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Weiche;

import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
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
public abstract class Funktionsdecoder extends Baustein implements Decoder
{
  /**
   * Zugeordnete Geräte.
   */
  @XmlElements({ @XmlElement(name = "Bahnuebergang", type = Bahnuebergang.class), @XmlElement(name = "HauptsignalRtGnGe", type = HauptsignalRtGnGe.class),
      @XmlElement(name = "HauptsignalRtGe", type = HauptsignalRtGe.class), @XmlElement(name = "HauptsignalRtGn", type = HauptsignalRtGn.class),
      @XmlElement(name = "Hauptsperrsignal", type = Hauptsperrsignal.class), @XmlElement(name = "Sperrsignal", type = Sperrsignal.class), @XmlElement(name = "Vorsignal", type = Vorsignal.class),
      @XmlElement(name = "Weiche", type = Weiche.class) })
  @JsonProperty
  protected SortedSet<Geraet> geraete = new TreeSet<>();

  /**
   * Konstruktor.
   * 
   * Wird nur während des JAXB-Unmarshalling aufgerufen.
   * 
   * @param byteAnzahl Anzahl belegter Bytes (Adressen)
   */
  protected Funktionsdecoder(int byteAnzahl)
  {
    super(byteAnzahl);
  }

  /**
   * Konstruktor für JPA.
   * 
   * Dieser Konstrktor ist auch für JAXB nötig, sollte hierdurch aber nie aufgerufen werden.
   */
  protected Funktionsdecoder()
  {
  }

  /**
   * Wert liefern: {@link #geraete}.
   * 
   * @return Wert
   */
  public SortedSet<Geraet> getGeraete()
  {
    return this.geraete;
  }

  /**
   * Alle Geräte als ValueChangeListener registrieren.
   */
  public void addValueChangedListeners()
  {
    for (Geraet geraet : this.geraete)
    {
      addValueChangedListener(geraet);
    }
  }
}

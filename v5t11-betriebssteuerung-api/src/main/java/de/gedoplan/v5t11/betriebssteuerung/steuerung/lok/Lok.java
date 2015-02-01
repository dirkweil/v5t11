package de.gedoplan.v5t11.betriebssteuerung.steuerung.lok;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.util.XmlPropertiesAdapter;
import de.gedoplan.v5t11.selectrix.SelectrixMessage;
import de.gedoplan.v5t11.selectrix.SelectrixMessageListener;

import java.util.Map;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;

/**
 * Lokomotive.
 *
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@JsonAutoDetect(JsonMethod.NONE)
public class Lok extends SingleIdEntity<Integer> implements Comparable<Lok>, SelectrixMessageListener
{
  /**
   * Bitmaske für Horn im Wert.
   */
  public static final int       MASK_HORN            = 0x80;

  /**
   * Bitmaske für Licht im Wert.
   */
  public static final int       MASK_LICHT           = 0x40;

  /**
   * Bitmaske für die Richtung im Wert.
   */
  public static final int       MASK_RICHTUNG        = 0x20;

  /**
   * Bitmaske für die Geschwindigkeit im Wert.
   */
  public static final int       MASK_GESCHWINDIGKEIT = 0x1F;

  /**
   * Maximalwert für die Geschwindigkeit.
   */
  public static final int       MAX_GESCHWINDIGKEIT  = 31;

  /**
   * Adresse der Lok am SX-Bus.
   *
   * Die Adresse stellt auch die Id des Objektes dar.
   */
  @Min(0)
  @Max(111)
  @XmlAttribute(name = "adr", required = true)
  @JsonProperty("adr")
  protected int                 adresse;

  /**
   * Name.
   */
  @XmlAttribute
  @JsonProperty
  protected String              name;

  /**
   * Bilddateiname.
   */
  @XmlAttribute(name = "bild")
  @JsonProperty("bild")
  protected String              bildFileName;

  /**
   * Decoder.
   */
  @XmlAttribute(required = true)
  @JsonProperty
  protected LokDecoder          decoder;

  /**
   * Konfigurationswerte.
   */
  @XmlElement(name = "properties")
  @XmlJavaTypeAdapter(XmlPropertiesAdapter.class)
  protected Map<String, String> properties;

  /**
   * Aktueller Wert.
   */
  @XmlAttribute
  @JsonProperty
  protected long                wert;

  // private transient ValueChangedListenerRegistry valueChangedListenerRegistry = new ValueChangedListenerRegistry();

  private Steuerung             steuerung;

  /**
   * Konstruktor.
   *
   * @param adresse Adresse
   * @param name Name
   * @param bildFileName Name der Bilddatei
   */
  public Lok(int adresse, String name, String bildFileName)
  {
    this.adresse = adresse;
    this.name = name;
    this.bildFileName = bildFileName;
  }

  /**
   * {@inheritDoc}
   *
   * @see de.gedoplan.baselibs.persistence.entity.SingleIdEntity#getId()
   */
  @Override
  public Integer getId()
  {
    return this.adresse;
  }

  /**
   * Wert liefern: {@link #adresse}.
   *
   * @return Wert
   */
  public int getAdresse()
  {
    return this.adresse;
  }

  /**
   * Wert liefern: {@link #name}.
   *
   * @return Wert
   */
  public String getName()
  {
    return this.name;
  }

  /**
   * Wert liefern: {@link #bildFileName}.
   *
   * @return Wert
   */
  public String getBildFileName()
  {
    return this.bildFileName;
  }

  /**
   * Wert liefern: {@link #decoder}.
   *
   * @return Wert
   */
  public LokDecoder getDecoder()
  {
    return this.decoder;
  }

  /**
   * Wert liefern: {@link #wert}.
   *
   * @return Wert
   */
  public long getWert()
  {
    return this.wert;
  }

  /**
   * Horn abfragen.
   *
   * @return boolean Einschaltzustand des Lokhorns
   */
  public boolean isHorn()
  {
    return isHorn(this.wert);
  }

  /**
   * Horn im übergebenen Wert abfragen.
   *
   * @param wert2 Wert
   * @return boolean Einschaltzustand des Lokhorns
   */
  public static boolean isHorn(long wert2)
  {
    return (wert2 & MASK_HORN) != 0;
  }

  /**
   * Horn ein/ausschalten.
   *
   * @param on Einschaltzustand des Lokhorns
   */
  public void setHorn(boolean on)
  {
    setWert(setHorn(this.wert, on));
  }

  /**
   * Horn im übergebenen Wert ein/ausschalten.
   *
   * @param wert2 Wert
   * @param on Einschaltzustand des Lokhorns
   */
  public static long setHorn(long wert2, boolean on)
  {
    if (on)
    {
      return (wert2 | MASK_HORN);
    }
    else
    {
      return (wert2 & ~MASK_HORN);
    }
  }

  /**
   * Licht abfragen.
   *
   * @return Einschaltzustand der Stirn/Schlußbeleuchtung
   */
  public boolean isLicht()
  {
    return isLicht(this.wert);
  }

  /**
   * Licht im übergebenen Wert abfragen.
   *
   * @param wert Wert
   * @return Einschaltzustand der Stirn/Schlußbeleuchtung
   */
  public static boolean isLicht(long wert)
  {
    return (wert & MASK_LICHT) != 0;
  }

  /**
   * Licht ein/ausschalten.
   *
   * @param on Einschaltzustand der Stirn/Schlußbeleuchtung
   */
  public void setLicht(boolean on)
  {
    setWert(setLicht(this.wert, on));
  }

  /**
   * Licht im übergebenen Wert ein/ausschalten.
   *
   * @param wert Wert
   * @param on Einschaltzustand der Stirn/Schlußbeleuchtung
   */
  public static long setLicht(long wert, boolean on)
  {
    if (on)
    {
      return (wert | MASK_LICHT);
    }
    else
    {
      return (wert & ~MASK_LICHT);
    }
  }

  /**
   * Fahrrichtung abfragen.
   *
   * @return <code>true</code>=rückwärts, <code>false</code>=vorwärts
   */
  public boolean isRueckwaerts()
  {
    return isRueckwaerts(this.wert);
  }

  /**
   * Fahrrichtung im übergebenen Wert abfragen.
   *
   * @param wert Wert
   * @return <code>true</code>=rückwärts, <code>false</code>=vorwärts
   */
  public static boolean isRueckwaerts(long wert)
  {
    return (wert & MASK_RICHTUNG) != 0;
  }

  /**
   * Fahrrichtung setzen.
   *
   * @param on <code>true</code>=rückwärts, <code>false</code>=vorwärts
   */
  public void setRueckwaerts(boolean on)
  {
    setWert(setRueckwaerts(this.wert, on));
  }

  /**
   * Fahrrichtung im übergebenen Wert ein/ausschalten.
   *
   * @param wert Wert
   * @param on <code>true</code>=rückwärts, <code>false</code>=vorwärts
   */
  public static long setRueckwaerts(long wert, boolean on)
  {
    if (on)
    {
      return (wert | MASK_RICHTUNG);
    }
    else
    {
      return (wert & ~MASK_RICHTUNG);
    }
  }

  /**
   * Geschwindigkeit abfragen.
   *
   * @return Fahrstufe (0..31)
   */
  public int getGeschwindigkeit()
  {
    return getGeschwindigkeit(this.wert);
  }

  /**
   * Geschwindigkeit im übergebenen Wert abfragen.
   *
   * @param wert Wert
   * @return Fahrstufe (0..31)
   */
  public static int getGeschwindigkeit(long wert)
  {
    return (int) (wert & MASK_GESCHWINDIGKEIT);
  }

  /**
   * Geschwindigkeit setzen.
   *
   * @param geschwindigkeit Fahrstufe (0..31)
   */
  public void setGeschwindigkeit(int geschwindigkeit)
  {
    setWert(setGeschwindigkeit(this.wert, geschwindigkeit));
  }

  /**
   * Geschwindigkeit im übergebenen Wert ein/ausschalten.
   *
   * @param wert Wert
   * @param geschwindigkeit Fahrstufe (0..31)
   */
  public static long setGeschwindigkeit(long wert, int geschwindigkeit)
  {
    if (geschwindigkeit < 0 || geschwindigkeit > MAX_GESCHWINDIGKEIT)
    {
      throw new IllegalArgumentException("Ungueltige Geschwindigkeit");
    }

    return (wert & ~MASK_GESCHWINDIGKEIT) | (geschwindigkeit & MASK_GESCHWINDIGKEIT);
  }

  /**
   * Wert liefern: {@link #properties}.
   *
   * @return Wert
   */
  public Map<String, String> getProperties()
  {
    return this.properties;
  }

  /**
   * Wert setzen: {@link #wert}.
   *
   * @param wert Wert
   */
  public void setWert(long wert)
  {
    setWert(wert, true);
  }

  /**
   * Wert setzen: {@link #wert}.
   *
   * @param wert Wert
   * @param updateInterface Änderung an Selectrix-Interface senden?
   */
  public void setWert(long wert, boolean updateInterface)
  {
    long old = this.wert;
    this.wert = wert;
    if (old != this.wert)
    {
      if (updateInterface && this.steuerung != null)
      {
        this.steuerung.setWert(this.adresse, (int) wert);
      }

      // this.valueChangedListenerRegistry.sendEvent(new ValueChangedEvent(this));
    }
  }

  /**
   * Listener für Werteänderungen hinzufügen.
   *
   * @param valueChangedListener Listener
   * @see de.gedoplan.v5t11.betriebssteuerung.event.ValueChangedListenerRegistry#addListener(de.gedoplan.v5t11.betriebssteuerung.event.ValueChangedListener)
   */
  // public void addValueChangedListener(ValueChangedListener valueChangedListener)
  // {
  // this.valueChangedListenerRegistry.addListener(valueChangedListener);
  // }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(Lok other)
  {
    return this.adresse - other.adresse;
  }

  /**
   * Konstruktor für interne Zwecke.
   */
  protected Lok()
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
    if (parent instanceof Steuerung)
    {
      this.steuerung = (Steuerung) parent;
    }
    else
    {
      throw new IllegalArgumentException("Illegal parent " + parent);
    }
  }

  @Override
  public void onMessage(SelectrixMessage message)
  {
    if (message.getAddress() == this.adresse)
    {
      setWert(message.getValue(), false);
    }
  }
}

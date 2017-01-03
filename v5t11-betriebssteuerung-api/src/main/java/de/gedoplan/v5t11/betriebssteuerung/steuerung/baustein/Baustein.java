package de.gedoplan.v5t11.betriebssteuerung.steuerung.baustein;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;
import de.gedoplan.baselibs.utils.exception.BugException;
import de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedEvent;
import de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedListener;
import de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedListenerRegistry;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.util.XmlPropertiesAdapter;
import de.gedoplan.v5t11.selectrix.SelectrixMessage;
import de.gedoplan.v5t11.selectrix.SelectrixMessageListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;

import lombok.Getter;
import lombok.Setter;

/**
 * Baustein am SX-Bus.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@JsonAutoDetect(JsonMethod.NONE)
public abstract class Baustein extends SingleIdEntity<String> implements Comparable<Baustein>, SelectrixMessageListener {
  /**
   * Id des Bausteins.
   */
  @Id
  @XmlAttribute(required = true)
  @JsonProperty
  @Getter
  protected String id;

  /**
   * Adresse des Bausteins am SX-Bus.
   */
  @Min(0)
  @Max(127)
  @XmlAttribute(name = "adr")
  @JsonProperty
  @Getter
  protected int adresse;

  /**
   * Anzahl genutzter Bytes ab {@link #adresse}.
   */
  @Min(1)
  @Max(8)
  @Getter
  private int byteAnzahl;

  /**
   * Einbauort.
   */
  @XmlAttribute
  @JsonProperty
  @Getter
  @Setter
  private String einbauOrt;

  /**
   * Konfigurationswerte.
   */
  @XmlElement(name = "properties")
  @XmlJavaTypeAdapter(XmlPropertiesAdapter.class)
  @Getter
  protected Map<String, String> properties;

  /**
   * Aktueller Wert.
   */
  @Min(0)
  @JsonProperty
  @Getter
  private long wert;

  private transient ValueChangedListenerRegistry valueChangedListenerRegistry;

  @Transient
  @Getter
  private Steuerung steuerung;

  @Transient
  protected AtomicReference<List<Integer>> adressCache = new AtomicReference<>();

  /**
   * Konstruktor.
   *
   * Wird nur während des JAXB-Unmarshalling aufgerufen.
   *
   * @param byteAnzahl
   *          Anzahl belegter Bytes (Adressen)
   */
  protected Baustein(int byteAnzahl) {
    if (byteAnzahl <= 0 || byteAnzahl > 8) {
      throw new IllegalArgumentException("Ungültige Byte-Anzahl: " + byteAnzahl);
    }

    this.byteAnzahl = byteAnzahl;
  }

  /**
   * Konstruktor für JPA.
   *
   * Dieser Konstruktor ist auch für JAXB nötig, sollte hierdurch aber nie
   * aufgerufen werden.
   */
  protected Baustein() {
    throw new BugException("Sollte nie aufgerufen werden");
  }

  /**
   * Alle belegten Adressen liefern.
   *
   * Die Ermittlung der Adressen geschieht 'LAZY', da zum Zeitpunkt der
   * Erzeugung des Objektes die Hauptadresse ggf. noch nicht
   * gesetzt ist.
   *
   * @return Adressen dieses Bausteins
   */
  public List<Integer> getAdressen() {
    if (this.adressCache.get() == null) {
      List<Integer> adressen = new ArrayList<>();
      for (int i = 0; i < this.byteAnzahl; ++i) {
        adressen.add(this.adresse + i);
      }
      this.adressCache.set(adressen);
    }
    return this.adressCache.get();
  }

  /**
   * Wert setzen: {@link #wert}.
   *
   * @param wert
   *          Wert
   */
  public void setWert(long wert) {
    setWert(wert, true);
  }

  /**
   * Wert setzen: {@link #wert}.
   *
   * @param wert
   *          Wert
   * @param updateInterface
   *          Änderung an Selectrix-Interface propagieren?
   */
  public void setWert(long wert, boolean updateInterface) {
    long old = this.wert;
    this.wert = wert;
    if (old != this.wert) {
      if (updateInterface && this.steuerung != null) {
        List<Integer> adressen = getAdressen();
        for (int offset = 0; offset < this.byteAnzahl; ++offset) {
          this.steuerung.setWert(adressen.get(offset), (int) (wert & 0b11111111L));
          wert >>>= 8;
        }
      }

      if (this.valueChangedListenerRegistry != null) {
        this.valueChangedListenerRegistry.sendEvent(new ValueChangedEvent(this));
      }
    }
  }

  /**
   * Listener für Werteänderungen hinzufügen.
   *
   * @param valueChangedListener
   *          Listener
   * @see de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedListenerRegistry#addListener(de.gedoplan.v5t11.betriebssteuerung.listener.ValueChangedListener)
   */
  public void addValueChangedListener(ValueChangedListener valueChangedListener) {
    if (this.valueChangedListenerRegistry == null) {
      this.valueChangedListenerRegistry = new ValueChangedListenerRegistry();
    }
    this.valueChangedListenerRegistry.addListener(valueChangedListener);
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(Baustein other) {
    return this.adresse - other.adresse;
  }

  /**
   * {@inheritDoc}
   *
   * @see de.gedoplan.v5t11.selectrix.SelectrixMessageListener#onMessage(de.gedoplan.v5t11.selectrix.SelectrixMessage)
   */
  @Override
  public void onMessage(SelectrixMessage message) {
    int address = message.getAddress();
    int offset = getAdressen().indexOf(address);
    if (offset >= 0) {
      int bitOffset = offset * 8;
      long mask = 0b11111111L << bitOffset;
      long value = (long) (message.getValue()) << bitOffset;
      setWert((getWert() & ~mask) | (value & mask), false);
    }

  }

  /**
   * Nachbearbeitung nach JAXB-Unmarshal.
   *
   * @param unmarshaller
   *          Unmarshaller
   * @param parent
   *          Parent
   */
  public void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    if (this.id == null) {
      throw new IllegalArgumentException("Id darf nicht null sein: " + this.toDebugString());
    }

    if (parent instanceof Steuerung) {
      this.steuerung = (Steuerung) parent;
    } else {
      throw new IllegalArgumentException("Illegal parent " + parent);
    }

    if (this.properties == null) {
      this.properties = new HashMap<>();
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return getClass().getSimpleName() + "{id=" + this.id + ",adresse=" + this.adresse + "}";
  }

  /**
   * Präfix für das Label liefern
   *
   * @return Präfix
   */
  public abstract String getLabelPrefix();

  /**
   * Label für die GUI erstellen.
   *
   * @return Label
   */
  public String getLabel() {
    if (this.id != null) {
      return getLabelPrefix() + " " + this.id;
    } else {
      return getLabelPrefix() + " " + getClass().getSimpleName();
    }
  }

  /**
   * Programmierfamilie liefern.
   *
   * Die Programmierfamilie ist die Identifizierung für Programmieralgorithmus und zugehörigen Dialog etc.
   * Als Konvention wird als Programmierfamilie der einfache Klassenname verwendet.
   *
   * @return Programmierfamilie oder <code>null</code>, falls nicht programmierbar.
   */
  public Class<?> getProgrammierfamilie() {
    return getClass();
  }

  /**
   * Ist der Baustein am SX-Bus angeschlossen?
   *
   * Der Default ist <code>true</code>. Die Ausnahme bilden Decoder in den
   * Fahrzeugen.
   *
   * @return <code>true</code>, wenn der Baustein am SX-Bus angeschlossen ist,
   *         <code>false</code>, wenn er am Gleis hängt.
   */
  public boolean isBusBaustein() {
    return true;
  }
}

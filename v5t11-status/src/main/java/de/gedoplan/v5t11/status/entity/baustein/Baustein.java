package de.gedoplan.v5t11.status.entity.baustein;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;
import de.gedoplan.baselibs.utils.util.ClassUtil;
import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

import jakarta.inject.Inject;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Baustein am SX-Bus.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public abstract class Baustein extends SingleIdEntity<String> implements Comparable<Baustein> {
  /**
   * Id des Bausteins.
   */
  @Id
  @XmlAttribute(required = true)
  @Getter(onMethod_ = @JsonbInclude)
  protected String id;

  /**
   * Label für die GUI erstellen.
   *
   * @return Label
   */
  @Getter
  private String label = getLabelPrefix() + " " + ClassUtil.getProxiedClass(getClass()).getSimpleName();

  /**
   * Erste Adresse des Bausteins am SX-Bus.
   */
  @Min(0)
  @Max(256)
  @XmlAttribute(name = "adr")
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
  @Getter
  @Setter
  private String einbauOrt;

  /**
   * Aktueller Wert.
   */
  @Min(0)
  @Getter
  protected long wert;

  @Transient
  @Getter
  protected Steuerung steuerung;

  @Transient
  protected AtomicReference<List<Integer>> adressen = new AtomicReference<>();

  @Inject
  protected EventFirer eventFirer;

  /**
   * Konstruktor.
   * <p>
   * Wird nur während des JAXB-Unmarshalling aufgerufen.
   *
   * @param byteAnzahl Anzahl belegter Bytes (Adressen)
   */
  protected Baustein(int byteAnzahl) {
    if (byteAnzahl < 0 || byteAnzahl > 8) {
      throw new IllegalArgumentException("Ungültige Byte-Anzahl: " + byteAnzahl);
    }

    this.byteAnzahl = byteAnzahl;
  }

  /**
   * Alle belegten Adressen liefern.
   * <p>
   * Die Ermittlung der Adressen geschieht 'LAZY', da zum Zeitpunkt der
   * Erzeugung des Objektes die Hauptadresse ggf. noch nicht
   * gesetzt ist.
   *
   * @return Adressen dieses Bausteins
   */
  public List<Integer> getAdressen() {
    if (this.adressen.get() == null) {
      List<Integer> adressen = new ArrayList<>();
      for (int i = 0; i < this.byteAnzahl; ++i) {
        adressen.add(this.adresse + i);
      }
      this.adressen.set(adressen);
    }
    return this.adressen.get();
  }

  /**
   * Wert setzen: {@link #wert}.
   *
   * @param wert Wert
   */
  public void setWert(long wert) {
    setWert(wert, true);
  }

  protected void setWert(long wert, boolean updateInterface) {
    long old = this.wert;
    this.wert = wert;
    if (old != this.wert) {
      if (updateInterface) {
        List<Integer> adressen = getAdressen();
        for (int offset = 0; offset < this.byteAnzahl; ++offset) {
          this.steuerung.setSX1Kanal(adressen.get(offset), (byte) (wert & 0b11111111L));
          wert >>>= 8;
        }
      }
    }
  }

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Comparable#compareTo(java.lang.Object)
   */
  @Override
  public int compareTo(Baustein other) {
    return this.id.compareTo(other.id);
  }

  /*
   * Nachbearbeitung nach JAXB-Unmarshal.
   */
  protected void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    if (this.id == null) {
      this.id = UUID.randomUUID().toString();
      this.label = getLabelPrefix() + " " + this.id.substring(0, this.id.indexOf('-'));
    } else {
      this.label = getLabelPrefix() + " " + this.id;
    }

    if (parent instanceof Steuerung) {
      this.steuerung = (Steuerung) parent;
    } else {
      throw new IllegalArgumentException("Illegal parent " + parent);
    }
  }

  /**
   * Präfix für das Label liefern
   *
   * @return Präfix
   */
  public abstract String getLabelPrefix();

  /**
   * Ist der Baustein am SX-Bus angeschlossen?
   * <p>
   * Der Default ist <code>true</code>. Die Ausnahme bilden Decoder in den
   * Fahrzeugen.
   *
   * @return <code>true</code>, wenn der Baustein am SX-Bus angeschlossen ist,
   *   <code>false</code>, wenn er am Gleis hängt.
   */
  public boolean isBusBaustein() {
    return true;
  }

  /**
   * Bausteinwert entsprechend Kanalwert aktualisieren.
   *
   * @param adr Adresse
   * @param kanalWert Wert
   */
  public void adjustWert(int adr, int kanalWert) {
    long mask = 0b11111111;
    long teilWert = (kanalWert) & mask;

    for (int bausteinAdresse : getAdressen()) {
      if (bausteinAdresse == adr) {
        this.wert = (this.wert & ~mask) | teilWert;

        adjustStatus();

        break;
      }

      mask <<= 8;
      teilWert <<= 8;
    }
  }

  /**
   * Status des Bausteins bzw. der angeschlossenen Elemente aktualisieren.
   */
  public abstract void adjustStatus();

  /**
   * Einfachen Klassennamen liefern. Wird für JSF-EL in der Bausteinprogrammierung verwendet.
   *
   * @return Klassenname
   */
  public String getSimpleClassName() {
    return getClass().getSimpleName();
  }
}

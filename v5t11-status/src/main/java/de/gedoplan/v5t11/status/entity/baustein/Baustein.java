package de.gedoplan.v5t11.status.entity.baustein;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;
import de.gedoplan.v5t11.status.entity.Steuerung;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;
import lombok.NoArgsConstructor;

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
  @Getter
  protected String id;

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
  private int byteAnzahl;

  /**
   * Einbauort.
   */
  @XmlAttribute
  @Getter
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
   * Alle belegten Adressen liefern.
   *
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
   * @param wert
   *          Wert
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
          this.steuerung.setKanalWert(adressen.get(offset), (int) (wert & 0b11111111L));
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
    return this.adresse - other.adresse;
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

  /**
   * Bausteinwert entsprechend Kanalwert aktualisieren.
   *
   * @param adr
   *          Adresse
   * @param kanalWert
   *          Wert
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
}

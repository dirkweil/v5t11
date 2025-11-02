package de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;
import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.util.domain.attribute.DecoderId;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbShort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlTransient;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;

@Entity
@Access(AccessType.FIELD)
@Table(name = Fahrzeug.TABLE_NAME)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Fahrzeug extends SingleIdEntity<String> {

  public static final String TABLE_NAME = "FZ_FAHRZEUG";
  public static final String TABLE_NAME_FUNKTIONEN = "FZ_FAHRZEUG_FUNKTION";
  public static final String TABLE_NAME_KONFIGURATIONEN = "FZ_FAHRZEUG_KONFIGURATION";

  @Getter
  @Setter
  @NotNull
  @Id
  private String betriebsnummer;

  // Fahrzeug ist/wird gelöscht
  // nur für temporäre Benachrichtigung; wird nicht in der DB gespeichert
  @Getter(onMethod_ = @JsonbShort)
  @Setter
  @Transient
  @XmlTransient
  private boolean removed;

  // Fahrzeug ist aktiv, d. h. in der Zentrale angemeldet
  @Getter
  @Setter(onMethod_ = @JsonbShort)
  @XmlTransient
  private boolean aktiv;

  // Aktuelle Fahrstufe
  @Getter
  @Setter(onMethod_ = @JsonbShort)
  @XmlTransient
  private int fahrstufe;

  @AssertTrue(message = "Ungültige Fahrstufe")
  @JsonbTransient
  boolean isfahrstufeValid() {
    return this.fahrstufe >= 0 && this.fahrstufe <= this.decoderId.getSystemTyp().getMaxFahrstufe();
  }

  // Rückwärtsfahrt
  @Getter
  @Setter(onMethod_ = @JsonbShort)
  @XmlTransient
  private boolean rueckwaerts;

  // Fahrlicht
  @Getter
  @Setter(onMethod_ = @JsonbShort)
  @XmlTransient
  private boolean licht;

  // Status der Funktionen (pro Funktion 1 Bit, nur 16 Bits relevant)
  @Column(name = "FKT_BITS", nullable = false)
  @Getter
  @Setter(onMethod_ = @JsonbShort)
  @XmlTransient
  private int fktBits;

  @Getter
  @Setter(onMethod_ = @JsonbShort)
  @Column(name = "LAST_CHANGE_MS")
  @XmlTransient
  private long lastChangeMillis;

  @Getter
  @Setter
  @XmlTransient
  @JsonbTransient
  @OneToMany(fetch = FetchType.EAGER)
  @JoinColumn(name = "zugfahrzeug_betriebsnummer")
  @OrderColumn(name = "fahrzeug_reihenfolge")
  private List<Fahrzeug> gezogeneFahrzeuge = new ArrayList<>();

  @XmlTransient
  @JsonbTransient
  @ManyToOne
  @JoinColumn(name = "zugfahrzeug_betriebsnummer", insertable = false, updatable = false)
  private Fahrzeug zugFahrzeug;

  /**
   * Typ des Fahrzeugs (Lok, Wagen, ...).
   */
  @Setter
  @Getter
  @Enumerated(EnumType.STRING)
  @NotNull
  private FahrzeugTyp fahrzeugTyp;

  @Getter
  @Setter
  private String beschreibung;

  @Lob
  @Getter
  @Setter
  @XmlTransient
  @JsonbTransient
  private Serializable image;

  @Getter
  @Setter
  private String decoderName;

  @Getter(onMethod_ = @JsonbShort)
  @Setter(onMethod_ = @JsonbShort)
  @NotNull
  private DecoderId decoderId;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = TABLE_NAME_FUNKTIONEN)
  @Getter
  @XmlElement(name = "funktion")
  private List<@NotNull FahrzeugFunktion> funktionen;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = TABLE_NAME_KONFIGURATIONEN)
  @OrderBy("nr")
  @Getter
  @XmlElement(name = "konfiguration")
  private List<@NotNull @Valid FahrzeugKonfiguration> konfigurationen;

  public Fahrzeug(String betriebsnummer) {
    this.betriebsnummer = betriebsnummer;
    this.funktionen = new ArrayList<>();
    this.konfigurationen = new ArrayList<>();
  }

  public Fahrzeug(String betriebsnummer, FahrzeugTyp fahrzeugTyp, String beschreibung, String decoderName, DecoderId decoderId, List<FahrzeugFunktion> funktionen,
    List<FahrzeugKonfiguration> konfigurationen) {
    this.betriebsnummer = betriebsnummer;
    this.fahrzeugTyp = fahrzeugTyp;
    this.beschreibung = beschreibung;
    this.decoderName = decoderName;
    this.decoderId = decoderId;
    this.funktionen = funktionen;
    this.konfigurationen = konfigurationen;
  }

  @Builder
  public Fahrzeug(String betriebsnummer, FahrzeugTyp fahrzeugTyp, String beschreibung, String decoderName, @NotNull SystemTyp systemTyp, int adresse,
    @Singular("funktion") List<FahrzeugFunktion> funktionen,
    @Singular("konfiguration") List<FahrzeugKonfiguration> konfigurationen) {
    this(betriebsnummer, fahrzeugTyp, beschreibung, decoderName, new DecoderId(systemTyp, adresse), funktionen, konfigurationen);
  }

  @Override
  @JsonbTransient
  public String getId() {
    return this.betriebsnummer;
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
  }

  public boolean copyStatus(Fahrzeug from) {
    boolean changed = (this.aktiv != from.aktiv);
    this.aktiv = from.aktiv;

    changed |= (this.fahrstufe != from.fahrstufe);
    this.fahrstufe = from.fahrstufe;

    changed |= (this.fktBits != from.fktBits);
    this.fktBits = from.fktBits;

    changed |= (this.licht != from.licht);
    this.licht = from.licht;

    changed |= (this.rueckwaerts != from.rueckwaerts);
    this.rueckwaerts = from.rueckwaerts;

    if (changed) {
      this.lastChangeMillis = from.lastChangeMillis;
    }

    return changed;
  }

  /*
   * Die folgenden Methoden dienen dazu, die alte XML-Form (bevor Betriebsnummer ID wurde)
   * noch lesen zu können.
   */
  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    if (this.fahrzeugTyp == null) {
      this.fahrzeugTyp =
        this.betriebsnummer.startsWith("RW-")
          ? FahrzeugTyp.WAGEN
          : this.betriebsnummer.endsWith("est")
            ? FahrzeugTyp.SONSTIGES
            : FahrzeugTyp.LOK;
    }
  }

  @XmlElement(name = "decoder")
  private String getDecoderNameFromOldDecoderElement() {
    return null;
  }

  private void setDecoderNameFromOldDecoderElement(String decoderName) {
    this.decoderName = decoderName;
  }

  @XmlElement(name = "id")
  private DecoderId getDecoderIdFromOldIdElement() {
    return null;
  }

  private void setDecoderIdFromOldIdElement(DecoderId decoderId) {
    this.decoderId = decoderId;
  }

}

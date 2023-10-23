package de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;
import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class Fahrzeug extends SingleIdEntity<FahrzeugId> {

  public static final String TABLE_NAME = "FZ_FAHRZEUG";
  public static final String TABLE_NAME_FUNKTIONEN = "FZ_FAHRZEUG_FUNKTION";
  public static final String TABLE_NAME_KONFIGURATIONEN = "FZ_FAHRZEUG_KONFIGURATION";

  // @Transient
  // @Inject
  // EventFirer eventFirer;

  @EmbeddedId
  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  private FahrzeugId id;

  // Fahrzeug ist/wird gelöscht
  // nur für temporäre Benachrichtigung; wird nicht in der DB gespeichert
  @Getter(onMethod_ = @JsonbInclude)
  @Setter
  @Transient
  @XmlTransient
  private boolean removed;

  // Fahrzeug ist aktiv, d. h. in der Zentrale angemeldet
  @Getter(onMethod_ = @JsonbInclude(full = true))
  @Setter(onMethod_ = @JsonbInclude)
  @XmlTransient
  private boolean aktiv;

  // Aktuelle Fahrstufe
  @Getter(onMethod_ = @JsonbInclude(full = true))
  @Setter(onMethod_ = @JsonbInclude)
  @XmlTransient
  private int fahrstufe;

  @AssertTrue(message = "Ungültige Fahrstufe")
  boolean isfahrstufeValid() {
    return this.fahrstufe >= 0 && this.fahrstufe <= this.id.getSystemTyp().getMaxFahrstufe();
  }

  // Rückwärtsfahrt
  @Getter(onMethod_ = @JsonbInclude(full = true))
  @Setter(onMethod_ = @JsonbInclude)
  @XmlTransient
  private boolean rueckwaerts;

  // Fahrlicht
  @Getter(onMethod_ = @JsonbInclude(full = true))
  @Setter(onMethod_ = @JsonbInclude)
  @XmlTransient
  private boolean licht;

  // Status der Funktionen (pro Funktion 1 Bit, nur 16 Bits releavant)
  @Column(name = "FKT_BITS", nullable = false)
  @Getter(onMethod_ = @JsonbInclude(full = true))
  @Setter(onMethod_ = @JsonbInclude)
  @XmlTransient
  private int fktBits;

  @Getter(onMethod_ = @JsonbInclude(full = true))
  @Setter(onMethod_ = @JsonbInclude)
  @Column(name = "LAST_CHANGE_MS")
  @XmlTransient
  private long lastChangeMillis;

  /**
   * Betriebsnummer des Fahrzeugs (DB-Nr. ö. ä.).
   */
  @Getter(onMethod_ = @JsonbInclude)
  @Setter
  @NotBlank
  @Column(nullable = false, unique = true)
  private String betriebsnummer;

  @Lob
  @Getter
  @Setter
  @XmlTransient
  private Serializable image;

  @Getter(onMethod_ = @JsonbInclude)
  @Setter
  private String decoder;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = TABLE_NAME_FUNKTIONEN)
  @Getter(onMethod_ = @JsonbInclude(full = true))
  @XmlElement(name = "funktion")
  private List<@NotNull FahrzeugFunktion> funktionen;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = TABLE_NAME_KONFIGURATIONEN)
  @OrderBy("nr")
  @Getter(onMethod_ = @JsonbInclude(full = true))
  @XmlElement(name = "konfiguration")
  private List<@NotNull @Valid FahrzeugKonfiguration> konfigurationen;

  public Fahrzeug(FahrzeugId id) {
    this.id = id;
    this.funktionen = new ArrayList<>();
    this.konfigurationen = new ArrayList<>();
  }

  public Fahrzeug(FahrzeugId id, String betriebsnummer, String decoder, List<FahrzeugFunktion> funktionen, List<FahrzeugKonfiguration> konfigurationen) {
    this.id = id;
    this.betriebsnummer = betriebsnummer;
    this.decoder = decoder;
    this.funktionen = funktionen;
    this.konfigurationen = konfigurationen;
  }

  @Builder
  public Fahrzeug(String betriebsnummer, String decoder, @NotNull SystemTyp systemTyp, int adresse,
    @Singular("funktion") List<FahrzeugFunktion> funktionen,
    @Singular("konfiguration") List<FahrzeugKonfiguration> konfigurationen) {
    this(new FahrzeugId(systemTyp, adresse), betriebsnummer, decoder, funktionen, konfigurationen);
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

}

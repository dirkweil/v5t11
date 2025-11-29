package de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug;

import de.gedoplan.v5t11.util.domain.attribute.DecoderAdr;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbShort;

import java.util.List;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Embeddable;
import jakarta.persistence.FetchType;
import jakarta.persistence.OrderBy;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
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

@Embeddable
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Fahrzeugdecoder {

  // Decoder ist aktiv, d. h. in der Zentrale angemeldet
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
    return this.fahrstufe >= 0 && this.fahrstufe <= this.decoderAdr.getSystemTyp().getMaxFahrstufe();
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

  // Letze Statusänderung
  @Getter(onMethod_ = @JsonbShort)
  @Setter(onMethod_ = @JsonbShort)
  private long lastChangeMillis;

  @Getter
  @Setter
  private String decoderName;

  @Getter(onMethod_ = @JsonbShort)
  @Setter(onMethod_ = @JsonbShort)
  @NotNull
  private DecoderAdr decoderAdr;

  @ElementCollection(fetch = FetchType.EAGER)
  //  @CollectionTable(name = TABLE_NAME_FUNKTIONEN)
  @Getter
  @XmlElement(name = "funktion")
  private List<@NotNull FahrzeugFunktion> funktionen;

  @ElementCollection(fetch = FetchType.EAGER)
  //  @CollectionTable(name = TABLE_NAME_KONFIGURATIONEN)
  @OrderBy("nr")
  @Getter
  @XmlElement(name = "konfiguration")
  private List<@NotNull @Valid FahrzeugKonfiguration> konfigurationen;

  //  public FahrzeugDecoder() {
  //    this.funktionen = new ArrayList<>();
  //    this.konfigurationen = new ArrayList<>();
  //  }

  public Fahrzeugdecoder(String decoderName, DecoderAdr decoderAdr, List<FahrzeugFunktion> funktionen, List<FahrzeugKonfiguration> konfigurationen) {
    this.decoderName = decoderName;
    this.decoderAdr = decoderAdr;
    this.funktionen = funktionen;
    this.konfigurationen = konfigurationen;
  }

  @Builder
  public Fahrzeugdecoder(String decoderName, @NotNull SystemTyp systemTyp, int adresse,
    @Singular("funktion") List<FahrzeugFunktion> funktionen,
    @Singular("konfiguration") List<FahrzeugKonfiguration> konfigurationen) {
    this(decoderName, new DecoderAdr(systemTyp, adresse), funktionen, konfigurationen);
  }

  public boolean copyStatus(Fahrzeugdecoder from) {
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

    return changed;
  }

}

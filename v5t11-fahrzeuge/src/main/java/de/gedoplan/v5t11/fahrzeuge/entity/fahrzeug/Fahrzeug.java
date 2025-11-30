package de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;
import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.util.domain.attribute.DecoderAdr;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbShort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
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
public class Fahrzeug extends SingleIdEntity<String> {

  public static final String TABLE_NAME = "FZ_FAHRZEUG";
  public static final String TABLE_NAME_FUNKTIONEN = "FZ_FAHRZEUG_FUNKTION";
  public static final String TABLE_NAME_KONFIGURATIONEN = "FZ_FAHRZEUG_KONFIGURATION";

  @Getter(onMethod_ = @JsonbShort)
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

  @Getter(onMethod_ = @JsonbShort)
  @Setter
  private Fahrzeugdecoder fahrzeugdecoder;

  public Fahrzeug(String betriebsnummer) {
    this.betriebsnummer = betriebsnummer;
  }

  public Fahrzeug(String betriebsnummer, FahrzeugTyp fahrzeugTyp, String beschreibung, String decoderName, DecoderAdr decoderAdr, List<FahrzeugFunktion> funktionen,
    List<FahrzeugKonfiguration> konfigurationen) {
    this.betriebsnummer = betriebsnummer;
    this.fahrzeugTyp = fahrzeugTyp;
    this.beschreibung = beschreibung;
    this.fahrzeugdecoder = new Fahrzeugdecoder(decoderName, decoderAdr, funktionen, konfigurationen);
  }

  @Builder
  public Fahrzeug(String betriebsnummer, FahrzeugTyp fahrzeugTyp, String beschreibung, String decoderName, @NotNull SystemTyp systemTyp, int adresse,
    @Singular("funktion") List<FahrzeugFunktion> funktionen,
    @Singular("konfiguration") List<FahrzeugKonfiguration> konfigurationen) {
    this(betriebsnummer, fahrzeugTyp, beschreibung, decoderName, new DecoderAdr(systemTyp, adresse), funktionen, konfigurationen);
  }

  @Override
  @JsonbTransient
  public String getId() {
    return this.betriebsnummer;
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
  }

  /*
   * Die folgenden Methoden dienen dazu, die alte XML-Form (bevor Betriebsnummer ID wurde)
   * noch lesen zu können.
   */
  @XmlElement(name = "decoderName")
  private String getOldDecoderName() {
    return null;
  }

  private void setOldDecoderName(String decoderName) {
    if (this.fahrzeugdecoder == null) {
      this.fahrzeugdecoder = createFahrzeugdecoder();
    }
    this.fahrzeugdecoder.setDecoderName(decoderName);
  }

  @XmlElement(name = "decoder")
  private String getDecoderNameFromOldDecoderElement() {
    return null;
  }

  private void setDecoderNameFromOldDecoderElement(String decoderName) {
    setOldDecoderName(decoderName);
  }

  @XmlElement(name = "decoderAdr")
  private String getOldDecoderAdr() {
    return null;
  }

  private void setOldDecoderAdr(String decoderAdrString) {
    if (this.fahrzeugdecoder == null) {
      this.fahrzeugdecoder = createFahrzeugdecoder();
    }
    this.fahrzeugdecoder.setDecoderAdr(DecoderAdr.fromString(decoderAdrString));
  }

  @XmlElement(name = "id")
  private String getDecoderAdrFromOldIdElement() {
    return null;
  }

  private void setDecoderAdrFromOldIdElement(String decoderAdrString) {
    setOldDecoderAdr(decoderAdrString);
  }

  @XmlElement(name = "funktion")
  private List<FahrzeugFunktion> getOldFunktionen() {
    return null;
  }

  private void setOldFunktionen(List<FahrzeugFunktion> funktionen) {
    if (this.fahrzeugdecoder == null) {
      this.fahrzeugdecoder = createFahrzeugdecoder();
    }
    this.fahrzeugdecoder.getFunktionen().clear();
    this.fahrzeugdecoder.getFunktionen().addAll(funktionen);
  }

  @XmlElement(name = "konfiguration")
  private List<FahrzeugKonfiguration> getOldKonfigurationen() {
    return null;
  }

  private void setOldKonfigurationen(List<FahrzeugKonfiguration> konfigurationen) {
    if (this.fahrzeugdecoder == null) {
      this.fahrzeugdecoder = createFahrzeugdecoder();
    }
    this.fahrzeugdecoder.getKonfigurationen().clear();
    this.fahrzeugdecoder.getKonfigurationen().addAll(konfigurationen);
  }

  private Fahrzeugdecoder createFahrzeugdecoder() {
    return new Fahrzeugdecoder(null, null, new ArrayList<>(), new ArrayList<>());
  }
}

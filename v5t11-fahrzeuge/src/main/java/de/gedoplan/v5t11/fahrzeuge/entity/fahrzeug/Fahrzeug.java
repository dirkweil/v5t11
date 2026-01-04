package de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;
import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.DecoderAdr;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbShort;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
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

  /**
   * Länge des Fahrzeugs in mm.
   */
  @Getter
  @Setter
  @JsonbTransient
  private int laenge;

  @Lob
  @Getter
  @Setter
  @XmlTransient
  @JsonbTransient
  private Serializable image;

  @Getter(onMethod_ = @JsonbShort)
  @Setter
  private Fahrzeugdecoder fahrzeugdecoder;

  /**
   * Id des Gleises, auf dem sich das Fahrzeug befindet.
   * <code>null</code> bedeutet "unbekannt".
   */
  @Getter
  @Setter
  @XmlTransient
  @JsonbTransient
  @AttributeOverride(name = "bereich", column = @Column(name = "GLEIS_BEREICH"))
  @AttributeOverride(name = "name", column = @Column(name = "GLEIS_NAME"))
  private BereichselementId gleisId;

  /**
   * Position auf dem Gleis.
   * Gemessen in mm vom Beginn (in Zählrichtung).
   */
  @Getter
  @Setter
  @XmlTransient
  @JsonbTransient
  @Column(name = "GLEIS_POSITION")
  private int gleisPosition;

  /**
   * Ist das Fahrzeug in Zählrichtung auf dem Gleis?
   * Ein Fahrzeug ist in Zählrichtung, wenn es bei Vorwärtsfahrt in Zählrichtung fährt.
   */
  @Getter
  @Setter
  @XmlTransient
  @JsonbTransient
  @Column(name = "GLEIS_ZAEHLRICHTUNG")
  private boolean gleisZaehlrichtung;

  public Fahrzeug(String betriebsnummer) {
    this.betriebsnummer = betriebsnummer;
  }

  public Fahrzeug(String betriebsnummer, FahrzeugTyp fahrzeugTyp, String beschreibung, int laenge, String decoderName, DecoderAdr decoderAdr, List<FahrzeugFunktion> funktionen,
    List<FahrzeugKonfiguration> konfigurationen) {
    this.betriebsnummer = betriebsnummer;
    this.fahrzeugTyp = fahrzeugTyp;
    this.beschreibung = beschreibung;
    this.laenge = laenge;
    this.fahrzeugdecoder = new Fahrzeugdecoder(decoderName, decoderAdr, funktionen, konfigurationen);
  }

  @Builder
  public Fahrzeug(String betriebsnummer, FahrzeugTyp fahrzeugTyp, String beschreibung, int laenge, String decoderName, @NotNull SystemTyp systemTyp, int adresse,
    @Singular("funktion") List<FahrzeugFunktion> funktionen,
    @Singular("konfiguration") List<FahrzeugKonfiguration> konfigurationen) {
    this(betriebsnummer, fahrzeugTyp, beschreibung, laenge, decoderName, new DecoderAdr(systemTyp, adresse), funktionen, konfigurationen);
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

  /*
   * Das Element decoderName liegt jetzt in fahrzeugdecoder.
   * Der Getter liefert immer null, damit es im generierten XML nicht vorkommt.
   * Der Setter speichert den Wert in fahrzeugdecoder.decoderName.
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

  /*
   * Das Element decoderName hieß noch früher einmal decoder.
   * Der Getter liefert immer null, damit es im generierten XML nicht vorkommt.
   * Der Setter speichert den Wert in fahrzeugdecoder.decoderName.
   */
  @XmlElement(name = "decoder")
  private String getDecoderNameFromOldDecoderElement() {
    return null;
  }

  private void setDecoderNameFromOldDecoderElement(String decoderName) {
    setOldDecoderName(decoderName);
  }

  /*
   * Das Element decoderAdr liegt jetzt in fahrzeugdecoder.
   * Der Getter liefert immer null, damit es im generierten XML nicht vorkommt.
   * Der Setter speichert den Wert in fahrzeugdecoder.decoderAdr.
   */
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

  /*
   * Das Element decoderAdr hieß noch früher einmal id.
   * Der Getter liefert immer null, damit es im generierten XML nicht vorkommt.
   * Der Setter speichert den Wert in fahrzeugdecoder.decoderName.
   */
  @XmlElement(name = "id")
  private String getDecoderAdrFromOldIdElement() {
    return null;
  }

  private void setDecoderAdrFromOldIdElement(String decoderAdrString) {
    setOldDecoderAdr(decoderAdrString);
  }

  /*
   * Die Elemente funktion und konfiguration liegen jetzt
   * in fahrzeugdecoder.
   * Listenelemente haben keinen Setter. Daher werden die
   * alten Werte in transiente Attribute angenommen und
   * in afterUnmarshal umgespeichert.
   * Bei einer Serialisierung sind die Werte null und tauchen
   * somit nicht im generierten XML auf.
   */
  @XmlElement(name = "funktion")
  @Transient
  private List<FahrzeugFunktion> oldFunktionen;

  @XmlElement(name = "konfiguration")
  @Transient
  private List<FahrzeugKonfiguration> oldKonfigurationen;

  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    if (this.fahrzeugTyp == null) {
      this.fahrzeugTyp =
        this.betriebsnummer.startsWith("RW-")
          ? FahrzeugTyp.WAGEN
          : this.betriebsnummer.endsWith("est")
            ? FahrzeugTyp.SONSTIGES
            : FahrzeugTyp.LOK;
    }

    if (this.oldFunktionen != null) {
      if (this.fahrzeugdecoder == null) {
        this.fahrzeugdecoder = createFahrzeugdecoder();
      }
      if (this.fahrzeugdecoder.getFunktionen().isEmpty()) {
        this.fahrzeugdecoder.getFunktionen().addAll(oldFunktionen);
      }
      this.oldFunktionen = null;
    }

    if (this.oldKonfigurationen != null) {
      if (this.fahrzeugdecoder == null) {
        this.fahrzeugdecoder = createFahrzeugdecoder();
      }
      if (this.fahrzeugdecoder.getKonfigurationen().isEmpty()) {
        this.fahrzeugdecoder.getKonfigurationen().addAll(oldKonfigurationen);
      }
      this.oldKonfigurationen = null;
    }
  }

  private Fahrzeugdecoder createFahrzeugdecoder() {
    return new Fahrzeugdecoder(null, null, new ArrayList<>(), new ArrayList<>());
  }

}

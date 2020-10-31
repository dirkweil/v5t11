package de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;
import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.domain.entity.SystemTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = Fahrzeug.TABLE_NAME)
@Access(AccessType.FIELD)
public class Fahrzeug extends SingleIdEntity<String> implements Comparable<Fahrzeug> {

  public static final String TABLE_NAME = "FZ_FAHRZEUG";
  public static final String TABLE_NAME_FUNKTION = "FZ_FAHRZEUG_FUNKTION";

  @Transient
  @Inject
  EventFirer eventFirer;

  /**
   * Id des Fahrzeugs (DB-Nr. ö. ä.).
   */
  @Id
  private String id;

  @Lob
  @Getter
  @Setter
  private Serializable image;

  @Getter
  private String decoder;

  /**
   * Systemtyp (Selectrix 1/2 oder NMRA-DCC)
   */
  @Enumerated(EnumType.STRING)
  @Column(name = "SYSTEM_TYP")
  @NotNull
  @Getter
  private SystemTyp systemTyp;

  /**
   * Kurze Adresse?
   * Hat nur bei DCC Bedeutung.
   */
  @Column(name = "KURZE_ADRESSE")
  private boolean kurzeAdresse;

  /**
   * Fahrzeugadresse.
   * Muss im gültigen Bereich sein:
   * - Selectrix: 1-103,
   * - Selecrix 2: 1-9999,
   * - DCC: 1-99 bei kurzer Adresse, 1-9999 sonst.
   */
  @Getter
  private int adresse;

  @AssertTrue(message = "Ungültige Adresse")
  boolean isAdresseValid() {
    if (this.systemTyp == null) {
      return true;
    }

    switch (this.systemTyp) {
    case SX1:
      return this.adresse >= 1 && this.adresse <= 103;
    case SX2:
    default:
      return this.adresse >= 1 && this.adresse <= 9999;
    case DCC:
      return this.adresse >= 1 && this.adresse <= (this.kurzeAdresse ? 99 : 9999);
    }
  }

  /**
   * Maximale Fahrstufe.
   * Gültige Werte:
   * - Selectrix: 31,
   * - Selecrix 2: 127,
   * - DCC: 14, 28 oder 126.
   */
  @Column(name = "MAX_FAHRSTUFE", nullable = false)
  @Getter(onMethod_ = @JsonbInclude(full = true))
  private int maxFahrstufe;

  @AssertTrue(message = "Ungültige maximale Fahrstufe")
  boolean isMaxFahrstufeValid() {
    if (this.systemTyp == null) {
      return true;
    }

    switch (this.systemTyp) {
    case SX1:
      return this.maxFahrstufe == 31;
    case SX2:
    default:
      return this.maxFahrstufe == 127;
    case DCC:
      return this.maxFahrstufe == 14 || this.maxFahrstufe == 28 || this.maxFahrstufe == 126;
    }
  }

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = TABLE_NAME_FUNKTION)
  @Getter(onMethod_ = @JsonbInclude(full = true))
  private Set<@NotNull FahrzeugFunktion> funktionen = new TreeSet<>((a, b) -> Integer.compare(a.nr, b.nr));

  @JsonbInclude
  @Override
  public String getId() {
    return this.id;
  }

  public Fahrzeug(String id, String decoder, @NotNull SystemTyp systemTyp, boolean kurzeAdresse, int adresse, int maxFahrstufe, FahrzeugFunktion... funktionen) {
    this.id = id;
    this.decoder = decoder;
    this.systemTyp = systemTyp;
    this.kurzeAdresse = kurzeAdresse;
    this.adresse = adresse;
    this.maxFahrstufe = maxFahrstufe;
    for (FahrzeugFunktion funktion : funktionen) {
      this.funktionen.add(funktion);
    }
  }

  protected Fahrzeug() {
  }

  @Override
  public int compareTo(Fahrzeug other) {
    return this.id.compareTo(other.id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Fahrzeug{");
    sb.append(this.id)
        .append('@')
        .append(this.adresse)
        .append('(')
        .append(this.systemTyp);
    if (this.systemTyp == SystemTyp.DCC && this.kurzeAdresse) {
      sb.append(",kurz");
    }
    sb.append(")}");
    return sb.toString();
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
  }

  @Embeddable
  @Getter(onMethod_ = @JsonbInclude(full = true))
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  @ToString
  public static class FahrzeugFunktion {
    private int nr;
    @NotNull
    @Enumerated(EnumType.STRING)
    private FahrzeugFunktionsGruppe gruppe;
    @NotEmpty
    private String beschreibung;
    private boolean impuls;
    private boolean horn;
    private int mask;
    private int value;

    public FahrzeugFunktion(int nr, FahrzeugFunktionsGruppe gruppe, String beschreibung, boolean impuls, boolean horn, int mask, int value) {
      this.nr = nr;
      this.gruppe = gruppe;
      this.beschreibung = beschreibung;
      this.impuls = impuls;
      this.horn = horn;
      this.mask = mask;
      this.value = value;
    }

    public FahrzeugFunktion(int nr, FahrzeugFunktionsGruppe gruppe, String beschreibung, boolean impuls, boolean horn) {
      this(nr, gruppe, beschreibung, impuls, horn, 1 << (nr - 1), 1 << (nr - 1));
    }

    @AllArgsConstructor
    @Getter
    public static enum FahrzeugFunktionsGruppe {
      FL("Fahrlicht"), BG("Betriebsgeräusch"), BA("Bahnsteigansage"), AF("Andere Funktion");

      private String name;
    }
  }

  // Ab hier Ist-Daten
  // TODO: Bleiben die hier?

  // @Transient
  // @Getter(onMethod_ = @JsonbInclude)
  // private boolean aktiv;
  //
  // @Getter(onMethod_ = @JsonbInclude)
  // private int fahrstufe;
  //
  // @AssertTrue(message = "Ungültige Fahrstufe")
  // boolean isfahrstufeValid() {
  // return this.fahrstufe >= 0 && this.fahrstufe <= this.maxFahrstufe;
  // }
  //
  // @Getter(onMethod_ = @JsonbInclude)
  // private boolean rueckwaerts;
  //
  // @Getter(onMethod_ = @JsonbInclude)
  // private boolean licht;
  //
  // @Column(name = "FUNKTION_STATUS")
  // @Getter(onMethod_ = @JsonbInclude)
  // private int funktionStatus;

}

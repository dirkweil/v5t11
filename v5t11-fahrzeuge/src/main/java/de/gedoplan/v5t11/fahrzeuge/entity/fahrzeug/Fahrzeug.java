package de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;
import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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
import javax.persistence.UniqueConstraint;
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
@Table(name = Fahrzeug.TABLE_NAME, uniqueConstraints = @UniqueConstraint(columnNames = { "SYSTEM_TYP", "ADRESSE" }))
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

    if (this.systemTyp == SystemTyp.SX1) {
      return this.adresse >= 1 && this.adresse <= 103;
    }

    return this.adresse >= 1 && this.adresse <= 9999;
  }

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = TABLE_NAME_FUNKTION)
  @Getter(onMethod_ = @JsonbInclude(full = true))
  private List<@NotNull FahrzeugFunktion> funktionen = new ArrayList<>();

  @JsonbInclude
  @Override
  public String getId() {
    return this.id;
  }

  public Fahrzeug(String id, String decoder, @NotNull SystemTyp systemTyp, int adresse, FahrzeugFunktion... funktionen) {
    this.id = id;
    this.decoder = decoder;
    this.systemTyp = systemTyp;
    this.adresse = adresse;
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
        .append(this.systemTyp)
        .append(")}");
    return sb.toString();
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
  }

  @Embeddable
  @Getter(onMethod_ = @JsonbInclude(full = true))
  @Setter
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  @ToString
  public static class FahrzeugFunktion {
    @NotNull
    @Enumerated(EnumType.STRING)
    private FahrzeugFunktionsGruppe gruppe;
    @NotEmpty
    private String beschreibung;
    private int maske;
    private int wert;
    private boolean impuls;
    private boolean horn;
    private boolean fader;

    public FahrzeugFunktion(FahrzeugFunktionsGruppe gruppe, int maske, int wert, boolean impuls, boolean horn, boolean fader, String beschreibung) {
      this.gruppe = gruppe;
      this.impuls = impuls;
      this.horn = horn;
      this.maske = maske;
      this.wert = wert;
      this.fader = fader;
      this.beschreibung = beschreibung;
    }

    public FahrzeugFunktion(FahrzeugFunktionsGruppe gruppe, int nr, boolean impuls, boolean horn, boolean fader, String beschreibung) {
      this(gruppe, 1 << (nr - 1), 1 << (nr - 1), impuls, horn, fader, beschreibung);
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + this.maske;
      result = prime * result + this.wert;
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      FahrzeugFunktion other = (FahrzeugFunktion) obj;
      if (this.maske != other.maske) {
        return false;
      }
      if (this.wert != other.wert) {
        return false;
      }
      return true;
    }

    @AllArgsConstructor
    @Getter
    public static enum FahrzeugFunktionsGruppe {
      FL("Fahrlicht"),
      FG("Fahrgeräusch"),
      BG("Betriebsgeräusch"),
      BA("Bahnsteigansage"),
      AF("Andere Funktion");

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

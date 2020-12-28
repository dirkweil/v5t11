package de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;
import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Access(AccessType.FIELD)
@Table(name = Fahrzeug.TABLE_NAME)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Fahrzeug extends SingleIdEntity<FahrzeugId> {

  public static final String TABLE_NAME = "FZ_FAHRZEUG";
  public static final String TABLE_NAME_FUNKTION = "FZ_FAHRZEUG_FUNKTION";

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
  private boolean removed;

  // Fahrzeug ist aktiv, d. h. in der Zentrale angemeldet
  @Getter(onMethod_ = @JsonbInclude(full = true))
  @Setter(onMethod_ = @JsonbInclude)
  private boolean aktiv;

  // Aktuelle Fahrstufe
  @Getter(onMethod_ = @JsonbInclude(full = true))
  @Setter(onMethod_ = @JsonbInclude)
  private int fahrstufe;

  @AssertTrue(message = "Ungültige Fahrstufe")
  boolean isfahrstufeValid() {
    return this.fahrstufe >= 0 && this.fahrstufe <= this.id.getSystemTyp().getMaxFahrstufe();
  }

  // Rückwärtsfahrt
  @Getter(onMethod_ = @JsonbInclude(full = true))
  @Setter(onMethod_ = @JsonbInclude)
  private boolean rueckwaerts;

  // Fahrlicht
  @Getter(onMethod_ = @JsonbInclude(full = true))
  @Setter(onMethod_ = @JsonbInclude)
  private boolean licht;

  // Status der Funktionen (pro Funktion 1 Bit, nur 16 Bits releavant)
  @Column(name = "FKT_BITS", nullable = false)
  @Getter(onMethod_ = @JsonbInclude(full = true))
  @Setter(onMethod_ = @JsonbInclude)
  private int fktBits;

  @Getter(onMethod_ = @JsonbInclude(full = true))
  @Setter(onMethod_ = @JsonbInclude)
  @Column(name = "LAST_CHANGE_MS")
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
  private Serializable image;

  @Getter(onMethod_ = @JsonbInclude)
  @Setter
  private String decoder;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = TABLE_NAME_FUNKTION)
  @Getter(onMethod_ = @JsonbInclude(full = true))
  private List<@NotNull FahrzeugFunktion> funktionen = new ArrayList<>();

  public Fahrzeug(FahrzeugId id, String betriebsnummer, String decoder, FahrzeugFunktion... funktionen) {
    this.id = id;
    this.betriebsnummer = betriebsnummer;
    this.decoder = decoder;
    for (FahrzeugFunktion funktion : funktionen) {
      this.funktionen.add(funktion);
    }
  }

  public Fahrzeug(String betriebsnummer, String decoder, @NotNull SystemTyp systemTyp, int adresse, FahrzeugFunktion... funktionen) {
    this(new FahrzeugId(systemTyp, adresse), betriebsnummer, decoder, funktionen);
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

}

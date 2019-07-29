package de.gedoplan.v5t11.status.entity.lok;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;
import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.status.entity.Kanal;
import de.gedoplan.v5t11.status.entity.SX2Kanal;
import de.gedoplan.v5t11.status.entity.SystemTyp;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

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
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = Lok.TABLE_NAME)
@Access(AccessType.FIELD)
public class Lok extends SingleIdEntity<String> implements Comparable<Lok> {

  public static final String TABLE_NAME = "V5T11_LOK";
  public static final String TABLE_NAME_FUNKTION = "V5T11_LOK_FUNKTION";

  @Transient
  @Inject
  EventFirer eventFirer;

  /**
   * Id der Lok (DB-Nr. ö. ä.).
   */
  @Id
  private String id;

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
   * Lokadresse.
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

  /**
   * Fahrstufe.
   */
  @Getter(onMethod_ = @JsonbInclude)
  private int fahrstufe;

  @AssertTrue(message = "Ungültige Fahrstufe")
  boolean isfahrstufeValid() {
    return this.fahrstufe >= 0 && this.fahrstufe <= this.maxFahrstufe;
  }

  @Getter(onMethod_ = @JsonbInclude)
  private boolean rueckwaerts;

  @Getter(onMethod_ = @JsonbInclude)
  private boolean licht;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = TABLE_NAME_FUNKTION)
  @Getter(onMethod_ = @JsonbInclude(full = true))
  private Set<@NotNull FunktionConfig> funktionConfigs = new TreeSet<>((a, b) -> Integer.compare(a.nr, b.nr));

  /**
   * Zustand der Funktionen.
   * Pro aktiver Funktion sind die entsprechenden Bits gesetzt.
   */
  @Column(name = "FUNKTION_STATUS")
  @Getter(onMethod_ = @JsonbInclude)
  private int funktionStatus;

  @JsonbInclude
  @Override
  public String getId() {
    return this.id;
  }

  @Transient
  @Getter(onMethod_ = @JsonbInclude)
  private boolean aktiv;

  public Lok(String id, String decoder, @NotNull SystemTyp systemTyp, boolean kurzeAdresse, int adresse, int maxFahrstufe, FunktionConfig... funktionConfigs) {
    this.id = id;
    this.decoder = decoder;
    this.systemTyp = systemTyp;
    this.kurzeAdresse = kurzeAdresse;
    this.adresse = adresse;
    this.maxFahrstufe = maxFahrstufe;
    for (FunktionConfig funktionConfig : funktionConfigs) {
      this.funktionConfigs.add(funktionConfig);
    }
    linkFunktionConfigs();
  }

  protected Lok() {
  }

  @PostLoad
  void linkFunktionConfigs() {
    this.funktionConfigs.forEach(fc -> fc.lok = this);
  }

  @Override
  public int compareTo(Lok other) {
    return this.id.compareTo(other.id);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("Lok{");
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

  public void setFahrstufe(int fahrstufe) {
    synchronized (Zentrale.class) {

      if (fahrstufe != this.fahrstufe) {
        if (fahrstufe < 0 || fahrstufe > this.maxFahrstufe) {
          throw new IllegalArgumentException("Ungültige Fahrstufe: " + fahrstufe);
        }

        this.fahrstufe = fahrstufe;
        this.eventFirer.fire(this);
      }

    }
  }

  public void setRueckwaerts(boolean rueckwaerts) {
    synchronized (Zentrale.class) {

      if (rueckwaerts != this.rueckwaerts) {
        this.rueckwaerts = rueckwaerts;
        this.eventFirer.fire(this);
      }

    }
  }

  public void setLicht(boolean licht) {
    synchronized (Zentrale.class) {

      if (licht != this.licht) {
        this.licht = licht;
        this.eventFirer.fire(this);
      }

    }
  }

  private void setFunktionStatus(int wert) {
    synchronized (Zentrale.class) {

      if (this.funktionStatus != wert) {
        this.funktionStatus = wert;
        this.eventFirer.fire(this);
      }

    }
  }

  public void setAktiv(boolean aktiv) {
    synchronized (Zentrale.class) {

      if (aktiv != this.aktiv) {
        this.aktiv = aktiv;
        this.eventFirer.fire(this);
      }

    }
  }

  public void reset() {
    synchronized (Zentrale.class) {

      boolean changed = this.fahrstufe != 0 || this.rueckwaerts || this.licht || this.funktionStatus != 0 || this.aktiv;
      this.fahrstufe = 0;
      this.rueckwaerts = false;
      this.licht = false;
      this.funktionStatus = 0;
      this.aktiv = false;
      if (changed) {
        this.eventFirer.fire(this);
      }

    }
  }

  /**
   * Lok-Zustand an SX1-Kanal anpassen.
   * Diese Methode wird nur für SX1-Loks aufgerufen.
   *
   * @param kanal SX1-Kanal
   */
  public void adjustTo(Kanal kanal) {
    synchronized (Zentrale.class) {

      if (this.systemTyp != SystemTyp.SX1) {
        throw new IllegalArgumentException("adjustTo(Kanal) kann nur für SX1-Loks aufgerufen werden");
      }

      if (this.adresse != kanal.getAdresse()) {
        throw new IllegalArgumentException("adjustTo(Kanal) fuer falsche Adresse aufgerufen");
      }

      int wert = kanal.getWert();

      // Falls irgendwas außer Grundzustand gemeldet wird, ist die Lok wohl aktiv
      if (wert != 0) {
        setAktiv(true);
      }

      if (this.aktiv) {
        setFahrstufe(wert & 0b0001_1111);

        setRueckwaerts((wert & 0b0010_0000) != 0);

        setLicht((wert & 0b0100_0000) != 0);

        boolean horn = (wert & 0b1000_0000) != 0;
        this.funktionConfigs.forEach(entry -> {
          if (entry.isHorn()) {
            entry.setAktiv(horn);
          }
        });
      }

    }
  }

  /**
   * Lok-Zustand an SX2-Kanal anpassen.
   * Diese Methode wird nicht für SX1-Loks aufgerufen.
   *
   * @param kanal SX2-Kanal
   */
  public void adjustTo(SX2Kanal kanal) {
    synchronized (Zentrale.class) {

      if (this.systemTyp == SystemTyp.SX1) {
        throw new IllegalArgumentException("adjustTo(SX2Kanal) darf nicht für SX1-Loks aufgerufen werden");
      }

      if (this.adresse != kanal.getAdresse()) {
        throw new IllegalArgumentException("adjustTo(SX2Kanal) fuer falsche Adresse aufgerufen");
      }

      // Falls irgendwas außer Grundzustand gemeldet wird, ist die Lok wohl aktiv
      if (kanal.getFahrstufe() != 0 || kanal.isRueckwaerts() || kanal.isLicht() || kanal.getFunktionStatus() != 0) {
        setAktiv(true);
      }

      if (this.aktiv) {
        setFahrstufe(kanal.getFahrstufe());

        setRueckwaerts(kanal.isRueckwaerts());

        setLicht(kanal.isLicht());

        setFunktionStatus(kanal.getFunktionStatus());
      }

    }
  }

  @Embeddable
  @Getter(onMethod_ = @JsonbInclude(full = true))
  @NoArgsConstructor(access = AccessLevel.PROTECTED)
  @ToString
  public static class FunktionConfig {
    private int nr;
    @NotNull
    @Enumerated(EnumType.STRING)
    private FunktionConfigGruppe gruppe;
    @NotEmpty
    private String beschreibung;
    private boolean impuls;
    private boolean horn;
    private int mask;
    private int value;

    @Transient
    @Getter(AccessLevel.NONE)
    private Lok lok;

    public FunktionConfig(int nr, FunktionConfigGruppe gruppe, String beschreibung, boolean impuls, boolean horn, int mask, int value) {
      this.nr = nr;
      this.gruppe = gruppe;
      this.beschreibung = beschreibung;
      this.impuls = impuls;
      this.horn = horn;
      this.mask = mask;
      this.value = value;
    }

    public FunktionConfig(int nr, FunktionConfigGruppe gruppe, String beschreibung, boolean impuls, boolean horn) {
      this(nr, gruppe, beschreibung, impuls, horn, 1 << (nr - 1), 1 << (nr - 1));
    }

    public boolean isAktiv() {
      return (this.lok.funktionStatus & this.mask) == this.value;
    }

    public void setAktiv(boolean aktiv) {
      int wert = this.lok.funktionStatus & ~this.mask;
      if (aktiv) {
        wert |= this.value;
      }
      this.lok.setFunktionStatus(wert);

    }

    @AllArgsConstructor
    @Getter
    public static enum FunktionConfigGruppe {
      LICHT("Fahrlicht"),
      SOUND("Betriebsgeräusch"),
      ANSAGE("Bahnsteigansage"),
      MISC("Andere Funktion");

      private String name;
    }
  }

}

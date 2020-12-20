package de.gedoplan.v5t11.status.entity.fahrzeug;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.status.entity.Kanal;
import de.gedoplan.v5t11.status.entity.SX2Kanal;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;
import de.gedoplan.v5t11.util.domain.entity.AbstractFahrzeug;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.inject.Inject;
import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Access(AccessType.FIELD)
@Table(name = Fahrzeug.TABLE_NAME)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Fahrzeug extends AbstractFahrzeug {

  public static final String TABLE_NAME = "ST_FAHRZEUG";

  @Transient
  @Inject
  EventFirer eventFirer;

  // Bit des Funktionsstatus, das das Signalhorn repräsentiert (können auch mehrere Bits sein)
  @Column(name = "HORN_BITS", nullable = false)
  @Getter(onMethod_ = @JsonbInclude(full = true))
  @Setter
  protected int hornBits;

  public Fahrzeug(FahrzeugId id) {
    super(id);
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
  }

  public void setFahrstufe(int fahrstufe) {
    synchronized (Zentrale.class) {

      if (fahrstufe != this.fahrstufe) {
        if (fahrstufe < 0 || fahrstufe > this.id.getSystemTyp().getMaxFahrstufe()) {
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

  public void setFunktionStatus(int wert) {
    synchronized (Zentrale.class) {

      if (this.fktBits != wert) {
        this.fktBits = wert;
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

      boolean changed = this.fahrstufe != 0 || this.rueckwaerts || this.licht || this.fktBits != 0 || this.aktiv;
      this.fahrstufe = 0;
      this.rueckwaerts = false;
      this.licht = false;
      this.fktBits = 0;
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
   * @param kanal
   *        SX1-Kanal
   */
  public void adjustTo(Kanal kanal) {
    synchronized (Zentrale.class) {

      if (this.id.getSystemTyp() != SystemTyp.SX1) {
        throw new IllegalArgumentException("adjustTo(Kanal) kann nur für SX1-Loks aufgerufen werden");
      }

      if (this.id.getAdresse() != kanal.getAdresse()) {
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
        if (horn) {
          this.fktBits |= this.hornBits;
        } else {
          this.fktBits &= (~this.hornBits);
        }
      }

    }
  }

  /**
   * Lok-Zustand an SX2-Kanal anpassen.
   * Diese Methode wird nicht für SX1-Loks aufgerufen.
   *
   * @param kanal
   *        SX2-Kanal
   */
  public void adjustTo(SX2Kanal kanal) {
    synchronized (Zentrale.class) {

      if (this.id.getSystemTyp() == SystemTyp.SX1) {
        throw new IllegalArgumentException("adjustTo(SX2Kanal) darf nicht für SX1-Loks aufgerufen werden");
      }

      if (this.id.getAdresse() != kanal.getAdresse()) {
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

}

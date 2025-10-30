package de.gedoplan.v5t11.status.entity.fahrzeug;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;
import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.status.entity.Kanal;
import de.gedoplan.v5t11.status.entity.SX2Kanal;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.domain.attribute.DecoderId;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbShort;

import jakarta.inject.Inject;
import jakarta.json.bind.annotation.JsonbTransient;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Entity-Klasse für Fahrzeuge.
 *
 * @author dw
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Fahrzeug extends SingleIdEntity<DecoderId> {

  // Fahrzeug
  @Inject
  EventFirer eventFirer;

  @Getter(onMethod_ = @JsonbShort)
  @Setter(onMethod_ = @JsonbShort)
  private DecoderId decoderId;

  // Fahrzeug ist aktiv, d. h. in der Zentrale angemeldet
  @Getter(onMethod_ = @JsonbShort)
  private boolean aktiv;

  // Aktuelle Fahrstufe
  @Getter(onMethod_ = @JsonbShort)
  private int fahrstufe;

  // Rückwärtsfahrt
  @Getter(onMethod_ = @JsonbShort)
  private boolean rueckwaerts;

  // Fahrlicht
  @Getter(onMethod_ = @JsonbShort)
  private boolean licht;

  // Status der Funktionen (pro Funktion 1 Bit, nur 16 Bits releavant)
  @Getter(onMethod_ = @JsonbShort)
  private int fktBits;

  // Letze Statusänderung
  @Getter(onMethod_ = @JsonbShort)
  @Setter(onMethod_ = @JsonbShort)
  private long lastChangeMillis;

  public Fahrzeug(DecoderId decoderId) {
    this.decoderId = decoderId;
    InjectionUtil.injectFields(this);
  }

  @JsonbTransient
  public DecoderId getId() {
    return this.decoderId;
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
  }

  public void setFahrstufe(int fahrstufe) {
    synchronized (Zentrale.class) {

      if (fahrstufe != this.fahrstufe) {
        if (fahrstufe < 0 || fahrstufe > this.decoderId.getSystemTyp().getMaxFahrstufe()) {
          throw new IllegalArgumentException("Ungültige Fahrstufe: " + fahrstufe);
        }

        this.lastChangeMillis = System.currentTimeMillis();
        this.fahrstufe = fahrstufe;

        this.eventFirer.fire(this, Changed.Literal.INSTANCE);
      }

    }
  }

  public void setRueckwaerts(boolean rueckwaerts) {
    synchronized (Zentrale.class) {

      if (rueckwaerts != this.rueckwaerts) {
        this.rueckwaerts = rueckwaerts;
        this.lastChangeMillis = System.currentTimeMillis();

        this.eventFirer.fire(this, Changed.Literal.INSTANCE);
      }

    }
  }

  public void setLicht(boolean licht) {
    synchronized (Zentrale.class) {

      if (licht != this.licht) {
        this.licht = licht;
        this.lastChangeMillis = System.currentTimeMillis();

        this.eventFirer.fire(this, Changed.Literal.INSTANCE);
      }

    }
  }

  public void setFktBits(int fktBits) {
    synchronized (Zentrale.class) {

      if (this.fktBits != fktBits) {
        this.fktBits = fktBits;
        this.lastChangeMillis = System.currentTimeMillis();

        this.eventFirer.fire(this, Changed.Literal.INSTANCE);
      }

    }
  }

  public void setAktiv(boolean aktiv) {
    synchronized (Zentrale.class) {

      if (aktiv != this.aktiv) {
        this.aktiv = aktiv;
        this.lastChangeMillis = System.currentTimeMillis();

        this.eventFirer.fire(this, Changed.Literal.INSTANCE);
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
        this.lastChangeMillis = System.currentTimeMillis();

        this.eventFirer.fire(this, Changed.Literal.INSTANCE);
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

      if (this.decoderId.getSystemTyp() != SystemTyp.SX1) {
        throw new IllegalArgumentException("adjustTo(Kanal) kann nur für SX1-Loks aufgerufen werden");
      }

      if (this.decoderId.getAdresse() != kanal.getAdresse()) {
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

      if (this.decoderId.getSystemTyp() == SystemTyp.SX1) {
        throw new IllegalArgumentException("adjustTo(SX2Kanal) darf nicht für SX1-Loks aufgerufen werden");
      }

      if (this.decoderId.getAdresse() != kanal.getAdresse()) {
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

        setFktBits(kanal.getFunktionStatus());
      }

    }
  }

}

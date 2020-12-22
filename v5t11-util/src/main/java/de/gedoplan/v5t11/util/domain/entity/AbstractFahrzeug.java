package de.gedoplan.v5t11.util.domain.entity;

import de.gedoplan.baselibs.persistence.entity.SingleIdEntity;
import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.MappedSuperclass;
import javax.validation.constraints.AssertTrue;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@MappedSuperclass
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractFahrzeug extends SingleIdEntity<FahrzeugId> {

  @EmbeddedId
  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  protected FahrzeugId id;

  protected AbstractFahrzeug(FahrzeugId id) {
    this.id = id;
  }

  // Fahrzeug ist aktiv, d. h. in der Zentrale angemeldet
  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  protected boolean aktiv;

  // Aktuelle Fahrstufe
  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  protected int fahrstufe;

  @AssertTrue(message = "Ungültige Fahrstufe")
  boolean isfahrstufeValid() {
    return this.fahrstufe >= 0 && this.fahrstufe <= this.id.getSystemTyp().getMaxFahrstufe();
  }

  // Rückwärtsfahrt
  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  protected boolean rueckwaerts;

  // Fahrlicht
  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  protected boolean licht;

  // Status der Funktionen (pro Funktion 1 Bit, nur 16 Bits releavant)
  @Column(name = "FKT_BITS", nullable = false)
  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  protected int fktBits;

}

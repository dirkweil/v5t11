package de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug;

import de.gedoplan.baselibs.persistence.entity.ToStringable;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@Getter(onMethod_ = @JsonbInclude(full = true))
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class FahrzeugKonfiguration extends ToStringable {
  @NotNull
  @Positive
  @EqualsAndHashCode.Include
  private Integer nr;

  private Integer soll;

  @Transient
  private Integer ist;

  @NotBlank
  @Column(columnDefinition = "TEXT")
  private String beschreibung;

  public FahrzeugKonfiguration(Integer nr, Integer soll, String beschreibung) {
    this.nr = nr;
    this.soll = soll;
    this.beschreibung = beschreibung;
  }
}

package de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug;

import de.gedoplan.baselibs.persistence.entity.ToStringable;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@Getter(onMethod_ = @JsonbInclude(full = true))
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@XmlAccessorType(XmlAccessType.FIELD)
public class FahrzeugKonfiguration extends ToStringable {
  @NotNull
  @Positive
  @EqualsAndHashCode.Include
  @XmlAttribute
  private Integer nr;

  @XmlAttribute
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

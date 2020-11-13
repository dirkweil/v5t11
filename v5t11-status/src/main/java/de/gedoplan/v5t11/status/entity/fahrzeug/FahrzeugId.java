package de.gedoplan.v5t11.status.entity.fahrzeug;

import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import java.io.Serializable;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Embeddable
@Access(AccessType.FIELD)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter(onMethod_ = @JsonbInclude)
@Setter(onMethod_ = @JsonbInclude)
@EqualsAndHashCode
@ToString
public class FahrzeugId implements Serializable, Comparable<FahrzeugId> {

  @NotNull
  @Enumerated(EnumType.STRING)
  private SystemTyp systemTyp;
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

  @Override
  public int compareTo(FahrzeugId o) {
    int diff = Integer.compare(this.adresse, o.adresse);
    if (diff != 0) {
      return diff;
    }

    return this.systemTyp.compareTo(o.systemTyp);
  }

}

package de.gedoplan.v5t11.leitstand.entity.fahrstrasse;

import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenelementTyp;
import de.gedoplan.v5t11.util.domain.entity.Bereichselement;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Convert;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Access(AccessType.FIELD)
@Table(name = Fahrstrasse.TABLE_NAME)
@NoArgsConstructor
public class Fahrstrasse extends Bereichselement {

  public static final String TABLE_NAME = "SW_FAHRSTRASSE";
  public static final String TABLE_NAME_ELEMENTE = "SW_FAHRSTRASSE_ELEMENTE";

  /**
   * Liste der Fahrstrassenelemente. Beginnt und endet immer mit einem Gleis.
   */
  @Getter(onMethod_ = @JsonbInclude(full = true))
  @Setter(onMethod_ = @JsonbInclude(full = true))
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = TABLE_NAME_ELEMENTE)
  private List<Fahrstrassenelement> elemente = new ArrayList<>();

  /**
   * Falls reserviert, Typ der Reservierung, sonst <code>null</code>.
   */
  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  @Convert(converter = FahrstrassenReservierungsTyp.Adapter4Jpa.class)
  private FahrstrassenReservierungsTyp reservierungsTyp = FahrstrassenReservierungsTyp.UNRESERVIERT;

  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  private int teilFreigabeAnzahl = 0;

  public String getShortName() {
    return getName().replaceAll("-W\\d+", "");
  }

  public Fahrstrassenelement getElement(Gleis gleis, boolean nurReserviert) {
    return getElement(gleis.getId(), FahrstrassenelementTyp.GLEIS, nurReserviert);
  }

  public Fahrstrassenelement getElement(Weiche weiche, boolean nurReserviert) {
    return getElement(weiche.getId(), FahrstrassenelementTyp.WEICHE, nurReserviert);
  }

  private Fahrstrassenelement getElement(BereichselementId elementId, FahrstrassenelementTyp elementTyp, boolean nurReserviert) {
    if (nurReserviert && this.reservierungsTyp == FahrstrassenReservierungsTyp.UNRESERVIERT) {
      return null;
    }

    int idx = 0;
    for (Fahrstrassenelement fe : getElemente()) {
      if (!fe.isSchutz()) {
        if (!nurReserviert || idx >= this.teilFreigabeAnzahl) {
          if (fe.getTyp() == elementTyp && fe.getId().equals(elementId)) {
            return fe;
          }
        }
      }

      ++idx;
    }

    return null;
  }
}

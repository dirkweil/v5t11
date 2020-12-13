package de.gedoplan.v5t11.leitstand.entity.fahrstrasse;

import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenelementTyp;
import de.gedoplan.v5t11.util.domain.entity.Bereichselement;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CollectionTable;
import javax.persistence.Convert;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Access(AccessType.FIELD)
@Table(name = Fahrstrasse.TABLE_NAME)
@NoArgsConstructor
public class Fahrstrasse extends Bereichselement {

  public static final String TABLE_NAME = "LS_FAHRSTRASSE";
  public static final String TABLE_NAME_ELEMENTE = "LS_FAHRSTRASSE_ELEMENTE";

  /**
   * Liste der Fahrstrassenelemente. Beginnt und endet immer mit einem Gleisabschnitt.
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

  public Fahrstrassenelement getElement(Gleisabschnitt gleisabschnitt, boolean nurReserviert) {
    return getElement(gleisabschnitt.getId(), FahrstrassenelementTyp.GLEISABSCHNITT, nurReserviert);
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

package de.gedoplan.v5t11.leitstand.entity.fahrstrasse;

import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.domain.entity.Bereichselement;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

public class Fahrstrasse extends Bereichselement {
  /**
   * In Zählrichtung orientiert?
   * Dieses Attribut dient nur als Default für die zugehörigen Fahrstrassenelemente.
   */
  @Getter(onMethod_ = @JsonbInclude(full = true))
  @Setter(onMethod_ = @JsonbInclude(full = true))
  private boolean zaehlrichtung;

  /**
   * Liste der Fahrstrassenelemente. Beginnt und endet immer mit einem Gleisabschnitt.
   */
  @Getter(onMethod_ = @JsonbInclude(full = true))
  @Setter(onMethod_ = @JsonbInclude(full = true))
  private List<Fahrstrassenelement> elemente = new ArrayList<>();

  /**
   * Falls reserviert, Typ der Reservierung, sonst <code>null</code>.
   */
  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  private FahrstrassenReservierungsTyp reservierungsTyp = FahrstrassenReservierungsTyp.UNRESERVIERT;

  @Getter(onMethod_ = @JsonbInclude)
  @Setter(onMethod_ = @JsonbInclude)
  private int teilFreigabeAnzahl = 0;

  public String getShortName() {
    return getName().replaceAll("-W\\d+", "");
  }

  public boolean contains(Gleisabschnitt gleisabschnitt, boolean nurReserviert) {
    if (nurReserviert && this.reservierungsTyp == FahrstrassenReservierungsTyp.UNRESERVIERT) {
      return false;
    }

    int idx = 0;
    for (Fahrstrassenelement fe : getElemente()) {
      if (!nurReserviert || idx >= this.teilFreigabeAnzahl) {
        if (gleisabschnitt.equals(fe.getFahrwegelement())) {
          return true;
        }
      }

      ++idx;
    }

    return false;
  }
}

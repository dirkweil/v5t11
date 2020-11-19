package de.gedoplan.v5t11.fahrstrassen.entity.fahrweg;

import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;

public interface ReservierbaresFahrwegelement {
  BereichselementId getReserviertefahrstrasseId();

  void setReserviertefahrstrasseId(BereichselementId fahrstrasseId);
}

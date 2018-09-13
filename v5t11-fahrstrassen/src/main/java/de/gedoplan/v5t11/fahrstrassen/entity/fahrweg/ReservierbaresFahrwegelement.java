package de.gedoplan.v5t11.fahrstrassen.entity.fahrweg;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;

public interface ReservierbaresFahrwegelement {
  Fahrstrasse getReserviertefahrstrasse();

  void setReserviertefahrstrasse(Fahrstrasse fahrstrasse);

  default void reserviereFuerFahrstrasse(Fahrstrasse fahrstrasse, boolean zaehlrichtung) {
    if (getReserviertefahrstrasse() != fahrstrasse) {
      setReserviertefahrstrasse(fahrstrasse);
      // this.zaehlrichtung = zaehlrichtung;
    }
  }
}

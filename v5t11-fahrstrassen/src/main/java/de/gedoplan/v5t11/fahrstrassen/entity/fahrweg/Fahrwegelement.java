package de.gedoplan.v5t11.fahrstrassen.entity.fahrweg;

import de.gedoplan.v5t11.fahrstrassen.entity.Bereichselement;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public abstract class Fahrwegelement extends Bereichselement {

  public Fahrwegelement(String bereich, String name) {
    super(bereich, name);
  }

  /**
   * Falls dieses Element Teil einer reservierten Fahrstrasse ist, diese Fahrstrasse, sonst <code>null</code>
   */
  @Getter
  protected Fahrstrasse reserviertefahrstrasse;

  /**
   * In Zählrichtung orientiert?
   */
  // TODO Wird das gebraucht?
  // private boolean zaehlrichtung;

  /**
   * Für Fahrstrasse als (un)reserviert markieren.
   *
   * @param fahrstrasse
   *          reservierte Fahrstrasse, zu der dieses Element gehört, oder <code>null</code>, wenn die Reservierung
   *          aufgehoben wird
   * @param zaehlrichtung
   *          Zählrichtung der reservierten Fahrstrasse
   */
  public void reserviereFuerFahrstrasse(Fahrstrasse fahrstrasse, boolean zaehlrichtung) {
    if (this.reserviertefahrstrasse != fahrstrasse) {
      this.reserviertefahrstrasse = fahrstrasse;
      // this.zaehlrichtung = zaehlrichtung;
    }
  }
}

package de.gedoplan.v5t11.fahrstrassen.entity.fahrweg;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractSignal;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class Signal extends AbstractSignal implements ReservierbaresFahrwegelement {

  @Getter
  @Setter
  @JsonbInclude
  private Set<SignalStellung> erlaubteStellungen;

  @Getter
  @Setter
  @JsonbInclude
  private String typ;

  /**
   * Falls dieses Element Teil einer reservierten Fahrstrasse ist, diese Fahrstrasse, sonst <code>null</code>
   */
  @Getter
  @Setter
  protected Fahrstrasse reserviertefahrstrasse;

  public Signal(String bereich, String name) {
    super(bereich, name);
  }

  public synchronized void copyStatus(Signal other) {
    this.stellung = other.stellung;
    if (other.erlaubteStellungen != null) {
      this.erlaubteStellungen = other.erlaubteStellungen;
    }
    if (other.typ != null) {
      this.typ = other.typ;
    }
  }

}
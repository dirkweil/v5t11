package de.gedoplan.v5t11.fahrstrassen.entity.fahrweg;

import de.gedoplan.v5t11.util.domain.SignalStellung;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class Signal extends Geraet {

  /**
   * Aktuelle Signalstellung.
   */
  @Getter
  @Setter(onMethod = @__(@JsonbInclude))
  private volatile SignalStellung stellung = SignalStellung.HALT;

  @Getter
  @Setter
  @JsonbInclude
  private Set<SignalStellung> erlaubteStellungen;

  @Getter
  @Setter
  @JsonbInclude
  private String typ;

  public Signal(String bereich, String name) {
    super(bereich, name);
  }

  public void copyStatus(Signal other) {
    this.stellung = other.stellung;
    if (other.erlaubteStellungen != null) {
      this.erlaubteStellungen = other.erlaubteStellungen;
    }
    if (other.typ != null) {
      this.typ = other.typ;
    }
  }
}

package de.gedoplan.v5t11.fahrstrassen.entity.fahrweg;

import de.gedoplan.v5t11.util.domain.WeichenStellung;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class Weiche extends Geraet {

  /**
   * Aktuelle Signalstellung.
   */
  @Getter
  @Setter(onMethod_ = @JsonbInclude)
  private volatile WeichenStellung stellung = WeichenStellung.GERADE;

  public Weiche(String bereich, String name) {
    super(bereich, name);
  }

  public void copyStatus(Weiche other) {
    this.stellung = other.stellung;
  }
}

package de.gedoplan.v5t11.strecken.entity.fahrweg;

import de.gedoplan.v5t11.util.domain.WeichenStellung;

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
  @Setter
  private WeichenStellung stellung = WeichenStellung.GERADE;

  public Weiche(String bereich, String name) {
    super(bereich, name);
  }

}

package de.gedoplan.v5t11.fahrstrassen.entity.fahrweg;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.geraet.AbstractWeiche;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@XmlAccessorType(XmlAccessType.NONE)
@NoArgsConstructor
public class Weiche extends AbstractWeiche implements ReservierbaresFahrwegelement {

  /**
   * Falls dieses Element Teil einer reservierten Fahrstrasse ist, diese Fahrstrasse, sonst <code>null</code>
   */
  @Getter
  @Setter
  protected Fahrstrasse reserviertefahrstrasse;

  public Weiche(String bereich, String name) {
    super(bereich, name);
  }

  public synchronized void copyStatus(Weiche other) {
    this.stellung = other.stellung;
  }

  // TODO Dies ist nur zum Testen; geht das auch eleganter?
  public void setStellung(WeichenStellung stellung) {
    this.stellung = stellung;
  }
}

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

  @Override
  public WeichenStellung getStellung() {
    throw new UnsupportedOperationException("Stellung wird nicht mit v5t11-status synchroniert");
  }

  @Override
  public void setStellung(WeichenStellung stellung) {
    throw new UnsupportedOperationException("Stellung wird nicht mit v5t11-status synchroniert");
  }

}

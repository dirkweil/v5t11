/*
 * Created on 11.04.2006 by dw
 */
package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.SignalTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class Sperrsignal extends Signal {
  /**
   * Konstruktor.
   */
  protected Sperrsignal() {
    super(1);
    addErlaubteStellung(SignalStellung.HALT, 0);
    addErlaubteStellung(SignalStellung.RANGIERFAHRT, 1);
    assert this.stellung2wert.keySet().containsAll(getTyp().getErlaubteStellungen());
  }

  @Override
  @JsonbInclude
  public SignalTyp getTyp() {
    return SignalTyp.SPERRSIGNAL;
  }
}

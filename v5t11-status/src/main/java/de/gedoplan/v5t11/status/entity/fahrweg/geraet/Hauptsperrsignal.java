/*
 * Created on 11.04.2006 by dw
 */
package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.SignalTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbInclude;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class Hauptsperrsignal extends Hauptsignal {
  /**
   * Konstruktor.
   */
  protected Hauptsperrsignal() {
    super(2);
    addErlaubteStellung(SignalStellung.HALT, 0);
    addErlaubteStellung(SignalStellung.FAHRT, 1);
    addErlaubteStellung(SignalStellung.LANGSAMFAHRT, 3);
    addErlaubteStellung(SignalStellung.RANGIERFAHRT, 2);
    assert this.stellung2wert.keySet().containsAll(getTyp().getErlaubteStellungen());
  }

  @Override
  @JsonbInclude
  public SignalTyp getTyp() {
    return SignalTyp.HAUPTSPERRSIGNAL;
  }
}

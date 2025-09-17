/*
 * Created on 11.04.2006 by dw
 */
package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.SignalTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbShort;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class Vorsignal extends Signal {
  /**
   * Konstruktor.
   */
  protected Vorsignal() {
    super(2);
  }

  @Override
  protected void addErlaubteStellungen() {
    addErlaubteStellung(SignalStellung.HALT, 0);
    addErlaubteStellung(SignalStellung.FAHRT, 3);
    addErlaubteStellung(SignalStellung.LANGSAMFAHRT, 2);
    addErlaubteStellung(SignalStellung.DUNKEL, 1);
  }

  @Override
  @JsonbShort
  public SignalTyp getTyp() {
    return SignalTyp.VORSIGNAL;
  }
}

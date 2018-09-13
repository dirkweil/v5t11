/*
 * Created on 11.04.2006 by dw
 */
package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class Vorsignal extends Signal {
  /**
   * Konstruktor.
   */
  protected Vorsignal() {
    super(2);
    addErlaubteStellung(SignalStellung.HALT, 0);
    addErlaubteStellung(SignalStellung.FAHRT, 3);
    addErlaubteStellung(SignalStellung.LANGSAMFAHRT, 2);
    addErlaubteStellung(SignalStellung.DUNKEL, 1);
  }
}

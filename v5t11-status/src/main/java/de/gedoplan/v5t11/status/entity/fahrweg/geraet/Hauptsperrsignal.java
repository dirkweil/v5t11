/*
 * Created on 11.04.2006 by dw
 */
package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class Hauptsperrsignal extends Hauptsignal {
  /**
   * Konstruktor.
   */
  protected Hauptsperrsignal() {
    super(2);
    addErlaubteStellung(Stellung.HALT, 0);
    addErlaubteStellung(Stellung.FAHRT, 1);
    addErlaubteStellung(Stellung.LANGSAMFAHRT, 3);
    addErlaubteStellung(Stellung.RANGIERFAHRT, 2);
  }
}

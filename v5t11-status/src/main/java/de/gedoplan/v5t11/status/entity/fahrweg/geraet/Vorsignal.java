/*
 * Created on 11.04.2006 by dw
 */
package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class Vorsignal extends Signal {
  /**
   * Konstruktor.
   */
  protected Vorsignal() {
    super(2);
    addErlaubteStellung(Stellung.HALT, 0);
    addErlaubteStellung(Stellung.FAHRT, 3);
    addErlaubteStellung(Stellung.LANGSAMFAHRT, 2);
    addErlaubteStellung(Stellung.DUNKEL, 1);
  }
}

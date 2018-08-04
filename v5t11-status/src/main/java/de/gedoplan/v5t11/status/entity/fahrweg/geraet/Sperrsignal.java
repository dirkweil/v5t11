/*
 * Created on 11.04.2006 by dw
 */
package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class Sperrsignal extends Signal {
  /**
   * Konstruktor.
   */
  protected Sperrsignal() {
    super(1);
    addErlaubteStellung(Stellung.HALT, 0);
    addErlaubteStellung(Stellung.RANGIERFAHRT, 1);
  }
}

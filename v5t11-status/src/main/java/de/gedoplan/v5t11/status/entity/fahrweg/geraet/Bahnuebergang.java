/*
 * Created on 11.04.2006 by dw
 */
package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class Bahnuebergang extends Signal {
  /**
   * Konstruktor.
   */
  protected Bahnuebergang() {
    super(1);
    addErlaubteStellung(Stellung.HALT, 0);
    addErlaubteStellung(Stellung.FAHRT, 1);
  }

  /**
   * Bahnübergang schliessen.
   *
   * Conveniance-Methode äquivalent zu {@link #setStellung(Stellung) setStellung(FAHRT)}.
   */
  public void schliessen() {
    setStellung(Stellung.FAHRT);
  }

  /**
   * Bahnübergang öffnen.
   *
   * Conveniance-Methode äquivalent zu {@link #setStellung(Stellung) setStellung(HALT)}.
   */
  public void oeffnen() {
    setStellung(Stellung.HALT);
  }
}

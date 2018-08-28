/*
 * Created on 11.04.2006 by dw
 */
package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import de.gedoplan.v5t11.util.domain.SignalStellung;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

@XmlAccessorType(XmlAccessType.NONE)
public class Bahnuebergang extends Signal {
  /**
   * Konstruktor.
   */
  protected Bahnuebergang() {
    super(1);
    addErlaubteStellung(SignalStellung.HALT, 0);
    addErlaubteStellung(SignalStellung.FAHRT, 1);
  }

  /**
   * Bahnübergang schliessen.
   *
   * Conveniance-Methode äquivalent zu {@link #setStellung(SignalStellung) setStellung(FAHRT)}.
   */
  public void schliessen() {
    setStellung(SignalStellung.FAHRT);
  }

  /**
   * Bahnübergang öffnen.
   *
   * Conveniance-Methode äquivalent zu {@link #setStellung(SignalStellung) setStellung(HALT)}.
   */
  public void oeffnen() {
    setStellung(SignalStellung.HALT);
  }
}

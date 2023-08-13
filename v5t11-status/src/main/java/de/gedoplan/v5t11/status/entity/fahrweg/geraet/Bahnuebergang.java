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
   * <p>
   * Conveniance-Methode äquivalent zu {@link #setStellung(SignalStellung) setStellung(FAHRT)}.
   */
  public void schliessen() {
    setStellung(SignalStellung.FAHRT);
  }

  /**
   * Bahnübergang öffnen.
   * <p>
   * Conveniance-Methode äquivalent zu {@link #setStellung(SignalStellung) setStellung(HALT)}.
   */
  public void oeffnen() {
    setStellung(SignalStellung.HALT);
  }

  @Override
  @JsonbInclude
  public SignalTyp getTyp() {
    return SignalTyp.BAHNUEBERGANG;
  }
}

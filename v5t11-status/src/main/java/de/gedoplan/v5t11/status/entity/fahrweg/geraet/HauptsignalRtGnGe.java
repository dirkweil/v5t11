/*
 * Created on 11.04.2006 by dw
 */
package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import de.gedoplan.v5t11.util.domain.SignalStellung;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Hauptsignal, 3-begriffig (Halt, Fahrt, Langsamfahrt).
 *
 * Das Signal belegt am Decoder SXSD1 zwei Ausgänge (Idx, Idx+1) und wird wie folgt angeschlossen:
 * <ul>
 * <li>Ausgang Idx: Pin1 = +5V, Pin3 = rot, Pin4 = grün</li>
 * <li>Ausgang Idx+1: Pin4 = gelb</li>
 * </ul>
 */
@XmlAccessorType(XmlAccessType.NONE)
public class HauptsignalRtGnGe extends Hauptsignal {
  /**
   * Konstruktor.
   */
  protected HauptsignalRtGnGe() {
    super(2);
    addErlaubteStellung(SignalStellung.HALT, 0);
    addErlaubteStellung(SignalStellung.FAHRT, 1);
    addErlaubteStellung(SignalStellung.LANGSAMFAHRT, 3);
  }
}

/*
 * Created on 11.04.2006 by dw
 */
package de.gedoplan.v5t11.status.entity.fahrweg.geraet;

import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Hauptsignal, 2-begriffig (Halt, Fahrt).
 *
 * Das Signal belegt am Decoder SXSD1 einen Ausgang und wird wie folgt angeschlossen: Pin1 = +5V, Pin3 = rot, Pin4 = grün
 */
@XmlAccessorType(XmlAccessType.NONE)
public class HauptsignalRtGn extends Hauptsignal {
  /**
   * Konstruktor.
   */
  protected HauptsignalRtGn() {
    super(1);
    addErlaubteStellung(SignalStellung.HALT, 0);
    addErlaubteStellung(SignalStellung.FAHRT, 1);
  }
}
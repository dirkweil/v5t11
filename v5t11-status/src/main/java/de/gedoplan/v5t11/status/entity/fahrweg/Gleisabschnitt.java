package de.gedoplan.v5t11.status.entity.fahrweg;

import de.gedoplan.v5t11.status.entity.baustein.Besetztmelder;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Gleisabschnitt.
 *
 * @author dw
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Gleisabschnitt extends Fahrwegelement {
  /**
   * Besetztmelder, der den Gleisabschnitt überwacht.
   */
  private Besetztmelder besetztmelder;

  /**
   * Anschluss am Besetztmelder (0, 1, ...)
   */
  @XmlAttribute(name = "idx")
  private int anschluss;

  /**
   * Gleisabschnitt besetzt?
   */
  private boolean besetzt;

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.getClass().getSimpleName()
        + "{"
        + this.bereich + "/" + this.name
        + " @ " + this.besetztmelder.getAdressen().get(0) + "/" + this.anschluss
        + "}";
  }

  /**
   * Nachbearbeitung nach JAXB-Unmarshal.
   *
   * @param unmarshaller
   *          Unmarshaller
   * @param parent
   *          Parent
   */
  @SuppressWarnings("unused")
  private void afterUnmarshal(Unmarshaller unmarshaller, Object parent) {
    if (parent instanceof Besetztmelder) {
      this.besetztmelder = (Besetztmelder) parent;
    } else {
      throw new IllegalArgumentException("Illegal parent " + parent);
    }
  }

}

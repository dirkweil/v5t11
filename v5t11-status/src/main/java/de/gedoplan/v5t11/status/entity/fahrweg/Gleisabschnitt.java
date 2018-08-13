package de.gedoplan.v5t11.status.entity.fahrweg;

import de.gedoplan.v5t11.status.entity.baustein.Besetztmelder;
import de.gedoplan.v5t11.status.jsonb.JsonbInclude;
import de.gedoplan.v5t11.status.util.EventFirer;

import javax.json.bind.annotation.JsonbTransient;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Gleisabschnitt extends Fahrwegelement {
  /**
   * Besetztmelder, der den Gleisabschnitt überwacht.
   */
  @JsonbTransient
  private Besetztmelder besetztmelder;

  /**
   * Anschluss am Besetztmelder (0, 1, ...)
   */
  @XmlAttribute(name = "idx")
  @JsonbTransient
  private int anschluss;

  /**
   * Gleisabschnitt besetzt?
   */
  @Getter(onMethod = @__(@JsonbInclude))
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

  public void adjustStatus() {
    boolean old = this.besetzt;
    this.besetzt = (this.besetztmelder.getWert() & (1 << this.anschluss)) != 0;
    if (old != this.besetzt) {
      EventFirer.fire(this);
    }
  }

}

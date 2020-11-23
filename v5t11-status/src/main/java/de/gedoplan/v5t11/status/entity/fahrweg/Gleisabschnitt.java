package de.gedoplan.v5t11.status.entity.fahrweg;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.v5t11.status.entity.baustein.Besetztmelder;
import de.gedoplan.v5t11.util.domain.entity.fahrweg.AbstractGleisabschnitt;

import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import org.jboss.logging.Logger;

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
public class Gleisabschnitt extends AbstractGleisabschnitt {
  /**
   * Besetztmelder, der den Gleisabschnitt überwacht.
   */
  private Besetztmelder besetztmelder;

  /**
   * Anschluss am Besetztmelder (0, 1, ...)
   */
  @XmlAttribute(name = "idx")
  private int anschluss;

  private Logger log = Logger.getLogger(getClass());

  /**
   * {@inheritDoc}
   *
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return this.getClass().getSimpleName()
        + "{"
        + getId().encode()
        + " [" + this.besetztmelder.getAdressen().get(0) + "/" + this.anschluss
        + "]}";
  }

  /**
   * Nachbearbeitung nach JAXB-Unmarshal.
   *
   * @param unmarshaller
   *        Unmarshaller
   * @param parent
   *        Parent
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
    boolean alt = isBesetzt();
    boolean neu = (this.besetztmelder.getWert() & (1 << this.anschluss)) != 0;
    if (alt != neu) {
      long currentTimeMillis = System.currentTimeMillis();
      if (currentTimeMillis - this.lastChangeMillis < 1000) {
        this.log.warn(this + ": Schnelle Statuswechsel");
      }
      this.lastChangeMillis = currentTimeMillis;

      setBesetzt(neu);
      this.eventFirer.fire(this);
    }
  }

  public void injectFields() {
    InjectionUtil.injectFields(this);
  }

}

package de.gedoplan.v5t11.leitstand.entity.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.leitstand.persistence.GleisabschnittRepository;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import lombok.Getter;

/**
 * Leeres Stellwerkselement
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public class StellwerkGleisabschnitt extends StellwerkElement {

  @XmlAttribute
  @Getter
  boolean label;

  @Inject
  GleisabschnittRepository gleisabschnittRepository;

  @Override
  public void addPersistentEntries() {
    super.addPersistentEntries();
    createIfNotPresent(this.gleisabschnittRepository, getId(), Gleisabschnitt::new);
  }

  /**
   * Zugehörigen Gleisabschnitt aus der DB lesen.
   * 
   * @return Gleisabschnitt
   */
  public Gleisabschnitt findGleisabschnitt() {
    Gleisabschnitt gleisabschnitt = this.gleisabschnittRepository.findById(getId());
    assert gleisabschnitt != null;
    return gleisabschnitt;
  }

}

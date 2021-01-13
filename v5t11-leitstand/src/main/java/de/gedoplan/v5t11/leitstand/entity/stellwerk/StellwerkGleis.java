package de.gedoplan.v5t11.leitstand.entity.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.leitstand.persistence.GleisRepository;

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
public class StellwerkGleis extends StellwerkElement {

  @XmlAttribute
  @Getter
  boolean label;

  @Inject
  GleisRepository gleisRepository;

  @Override
  public void addPersistentEntries() {
    super.addPersistentEntries();
    createIfNotPresent(this.gleisRepository, getId(), Gleis::new);
  }

  /**
   * Zugehörigen Gleis aus der DB lesen.
   * 
   * @return Gleis
   */
  public Gleis findGleis() {
    Gleis gleis = this.gleisRepository.findById(getId());
    assert gleis != null;
    return gleis;
  }

}

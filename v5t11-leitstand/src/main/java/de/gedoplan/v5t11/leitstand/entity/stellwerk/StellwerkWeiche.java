package de.gedoplan.v5t11.leitstand.entity.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.persistence.GleisRepository;
import de.gedoplan.v5t11.leitstand.persistence.WeicheRepository;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import lombok.Getter;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Leeres Stellwerkselement
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public class StellwerkWeiche extends StellwerkElement {

  @Inject
  GleisRepository gleisRepository;

  @Inject
  WeicheRepository weicheRepository;

  @Getter
  private BereichselementId gleisId;

  @Override
  public void addPersistentEntries() {
    super.addPersistentEntries();

    Weiche weiche = createIfNotPresent(this.weicheRepository, getId(), Weiche::new);

    this.gleisId = new BereichselementId(getBereich(), weiche.getGleisName());
    createIfNotPresent(this.gleisRepository, this.gleisId, Gleis::new);
  }

  /**
   * Zugehörige Weiche aus der DB lesen.
   *
   * @return Weiche
   */
  public Weiche findWeiche() {
    Weiche weiche = this.weicheRepository.findById(this.getId());
    assert weiche != null;
    return weiche;
  }

  /**
   * Zugehörigen Gleis aus der DB lesen.
   *
   * @return Gleis
   */
  public Gleis findGleis() {
    Gleis gleis = this.gleisRepository.findById(this.gleisId);
    assert gleis != null;
    return gleis;
  }
}

package de.gedoplan.v5t11.leitstand.entity.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.persistence.GleisRepository;
import de.gedoplan.v5t11.leitstand.persistence.WeicheRepository;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;

import javax.inject.Inject;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Stellwerkselement für eine Doppelkreuzungsweiche mit 2 Antrieben.
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public class StellwerkDkw2 extends StellwerkElement {

  @Inject
  GleisRepository gleisRepository;

  @Inject
  WeicheRepository weicheRepository;

  private BereichselementId weicheAId;

  private BereichselementId weicheBId;

  private BereichselementId gleisId;

  @Override
  public void addPersistentEntries() {
    super.addPersistentEntries();

    this.weicheAId = new BereichselementId(getBereich(), getName() + "a");
    Weiche weicheA = createIfNotPresent(this.weicheRepository, this.weicheAId, Weiche::new);
    this.weicheBId = new BereichselementId(getBereich(), getName() + "b");
    createIfNotPresent(this.weicheRepository, this.weicheBId, Weiche::new);

    this.gleisId = new BereichselementId(getBereich(), weicheA.getGleisName());
    createIfNotPresent(this.gleisRepository, this.gleisId, Gleis::new);
  }

  /**
   * Zugehörige Weiche aus der DB lesen.
   * 
   * @return Weiche
   */
  public Weiche findWeicheA() {
    Weiche weiche = this.weicheRepository.findById(this.weicheAId);
    assert weiche != null;
    return weiche;
  }

  /**
   * Zugehörige Weiche aus der DB lesen.
   * 
   * @return Weiche
   */
  public Weiche findWeicheB() {
    Weiche weiche = this.weicheRepository.findById(this.weicheBId);
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

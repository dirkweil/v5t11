package de.gedoplan.v5t11.leitstand.entity.stellwerk;

import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.leitstand.persistence.GleisabschnittRepository;
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
  GleisabschnittRepository gleisabschnittRepository;

  @Inject
  WeicheRepository weicheRepository;

  private BereichselementId weicheAId;

  private BereichselementId weicheBId;

  private BereichselementId gleisabschnittId;

  @Override
  public void addPersistentEntries() {
    super.addPersistentEntries();

    this.weicheAId = new BereichselementId(getBereich(), getName() + "a");
    Weiche weicheA = createIfNotPresent(this.weicheRepository, this.weicheAId, Weiche::new);
    this.weicheBId = new BereichselementId(getBereich(), getName() + "b");
    createIfNotPresent(this.weicheRepository, this.weicheBId, Weiche::new);

    this.gleisabschnittId = new BereichselementId(getBereich(), weicheA.getGleisabschnittName());
    createIfNotPresent(this.gleisabschnittRepository, this.gleisabschnittId, Gleisabschnitt::new);
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
   * Zugehörigen Gleisabschnitt aus der DB lesen.
   * 
   * @return Gleisabschnitt
   */
  public Gleisabschnitt findGleisabschnitt() {
    Gleisabschnitt gleisabschnitt = this.gleisabschnittRepository.findById(this.gleisabschnittId);
    assert gleisabschnitt != null;
    return gleisabschnitt;
  }
}

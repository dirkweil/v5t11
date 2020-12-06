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
 * Leeres Stellwerkselement
 *
 * @author dw
 */
@XmlAccessorType(XmlAccessType.NONE)
public class StellwerkWeiche extends StellwerkElement {

  @Inject
  GleisabschnittRepository gleisabschnittRepository;

  @Inject
  WeicheRepository weicheRepository;

  private BereichselementId gleisabschnittId;

  @Override
  public void addPersistentEntries() {
    super.addPersistentEntries();

    Weiche weiche = createIfNotPresent(this.weicheRepository, getId(), Weiche::new);

    this.gleisabschnittId = new BereichselementId(getBereich(), weiche.getGleisabschnittName());
    createIfNotPresent(this.gleisabschnittRepository, this.gleisabschnittId, Gleisabschnitt::new);
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

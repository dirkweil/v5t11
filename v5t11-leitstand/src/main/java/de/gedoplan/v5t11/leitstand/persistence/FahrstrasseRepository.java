package de.gedoplan.v5t11.leitstand.persistence;

import de.gedoplan.baselibs.persistence.repository.SingleIdEntityRepository;
import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenelementTyp;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
@Transactional(rollbackOn = Exception.class)
public class FahrstrasseRepository extends SingleIdEntityRepository<BereichselementId, Fahrstrasse> {

  public List<Fahrstrasse> findByGleisabschnitt(Gleisabschnitt gleisabschnitt) {
    return this.entityManager
        .createQuery("select distinct fs "
            + "from Fahrstrasse fs "
            + "join fs.elemente e "
            + "where e.typ=:typ and e.id=:id", Fahrstrasse.class)
        .setParameter("typ", FahrstrassenelementTyp.GLEISABSCHNITT)
        .setParameter("id", gleisabschnitt.getId())
        .getResultList();
  }

}

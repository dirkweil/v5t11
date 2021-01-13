package de.gedoplan.v5t11.fahrstrassen.persistence;

import de.gedoplan.baselibs.persistence.repository.SingleIdEntityRepository;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
@Transactional(rollbackOn = Exception.class)
public class GleisRepository extends SingleIdEntityRepository<BereichselementId, Gleis> {

  public Gleis findByBereichAndName(String bereich, String name) {
    return findById(new BereichselementId(bereich, name));
  }

  public long findMaxLastChangeMillis() {
    Long max = findSingle("select max(x.lastChangeMillis) from Gleis x", Long.class);
    return max != null ? max : 0L;
  }

}

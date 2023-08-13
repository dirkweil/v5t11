package de.gedoplan.v5t11.fahrstrassen.persistence;

import de.gedoplan.baselibs.persistence.repository.SingleIdEntityRepository;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;

import java.util.Optional;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional(rollbackOn = Exception.class)
public class GleisRepository extends SingleIdEntityRepository<BereichselementId, Gleis> {

  public Optional<Gleis> findByBereichAndName(String bereich, String name) {
    return findById(new BereichselementId(bereich, name));
  }
}

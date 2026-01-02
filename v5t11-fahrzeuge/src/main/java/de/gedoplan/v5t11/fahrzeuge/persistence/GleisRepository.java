package de.gedoplan.v5t11.fahrzeuge.persistence;

import de.gedoplan.baselibs.persistence.repository.SingleIdEntityRepository;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional(rollbackOn = Exception.class)
public class GleisRepository extends SingleIdEntityRepository<BereichselementId, Gleis> {

}

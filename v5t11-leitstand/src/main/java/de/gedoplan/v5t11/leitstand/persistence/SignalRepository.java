package de.gedoplan.v5t11.leitstand.persistence;

import de.gedoplan.baselibs.persistence.repository.SingleIdEntityRepository;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

@ApplicationScoped
@Transactional(rollbackOn = Exception.class)
public class SignalRepository extends SingleIdEntityRepository<BereichselementId, Signal> {

}
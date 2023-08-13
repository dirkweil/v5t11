package de.gedoplan.v5t11.fahrstrassen.persistence;

import de.gedoplan.baselibs.persistence.repository.SingleIdEntityRepository;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.FahrstrassenStatus;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
@Transactional(rollbackOn = Exception.class)
public class FahrstrassenStatusRepository extends SingleIdEntityRepository<BereichselementId, FahrstrassenStatus> {

}

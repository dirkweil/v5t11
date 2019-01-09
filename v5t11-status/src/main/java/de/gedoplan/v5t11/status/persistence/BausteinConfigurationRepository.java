package de.gedoplan.v5t11.status.persistence;

import de.gedoplan.baselibs.persistence.repository.SingleIdEntityRepository;
import de.gedoplan.v5t11.status.entity.BausteinConfiguration;

import javax.enterprise.context.ApplicationScoped;

/**
 * DB-Repository für {@link BausteinConfiguration}.
 *
 * @author dw
 */
@ApplicationScoped
public class BausteinConfigurationRepository extends SingleIdEntityRepository<String, BausteinConfiguration> {

}

package de.gedoplan.v5t11.status.persistence;

import de.gedoplan.baselibs.persistence.repository.SingleIdEntityRepository;
import de.gedoplan.v5t11.status.entity.lok.Lok;

import javax.enterprise.context.ApplicationScoped;

/**
 * DB-Repository für {@link Lok}.
 *
 * @author dw
 */
@ApplicationScoped
public class LokRepository extends SingleIdEntityRepository<String, Lok> {

}

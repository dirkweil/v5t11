package de.gedoplan.v5t11.status.persistence;

import de.gedoplan.baselibs.persistence.repository.SingleIdEntityRepository;
import de.gedoplan.v5t11.status.entity.Kanal;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

/**
 * DB-Repository für {@link Kanal}.
 *
 * @author dw
 */
@ApplicationScoped
@Transactional
public class KanalRepository extends SingleIdEntityRepository<Integer, Kanal> {

}

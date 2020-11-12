package de.gedoplan.v5t11.status.persistence;

import de.gedoplan.baselibs.persistence.repository.SingleIdEntityRepository;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.status.entity.fahrzeug.FahrzeugId;

import javax.enterprise.context.ApplicationScoped;

/**
 * DB-Repository für {@link Fahrzeug}.
 *
 * @author dw
 */
@ApplicationScoped
public class FahrzeugRepository extends SingleIdEntityRepository<FahrzeugId, Fahrzeug> {

}

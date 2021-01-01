package de.gedoplan.v5t11.fahrzeuge.persistence;

import de.gedoplan.baselibs.persistence.repository.SingleIdEntityRepository;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.transaction.Transactional;

/**
 * DB-Repository für {@link Fahrzeug}.
 *
 * @author dw
 */
@ApplicationScoped
@Transactional(rollbackOn = Exception.class)
public class FahrzeugRepository extends SingleIdEntityRepository<FahrzeugId, Fahrzeug> {

  public List<Fahrzeug> findAllSortedByBetriebsnummer() {
    return findMulti("select x from Fahrzeug x order by x.betriebsnummer");
  }
}

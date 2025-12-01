package de.gedoplan.v5t11.fahrzeuge.persistence;

import de.gedoplan.baselibs.persistence.repository.SingleIdEntityRepository;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.domain.attribute.DecoderAdr;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

/**
 * DB-Repository für {@link Fahrzeug}.
 *
 * @author dw
 */
@ApplicationScoped
@Transactional(rollbackOn = Exception.class)
public class FahrzeugRepository extends SingleIdEntityRepository<String, Fahrzeug> {

  public List<Fahrzeug> findAllSortedByBetriebsnummer() {
    return findMulti("select x from Fahrzeug x order by x.betriebsnummer");
  }

  public List<Fahrzeug> findOhneZugFahrzeugSortedByBetriebsnummer() {
    return findMulti("select x from Fahrzeug x where x.zugFahrzeug is null order by x.betriebsnummer");
  }

  public List<Fahrzeug> findByDecoderAdr(DecoderAdr decoderAdr) {
    TypedQuery<Fahrzeug> query = this.entityManager
      .createQuery("select x from Fahrzeug x where x.fahrzeugdecoder.decoderAdr=?1", Fahrzeug.class)
      .setParameter(1, decoderAdr);
    return findMulti(query);
  }
}

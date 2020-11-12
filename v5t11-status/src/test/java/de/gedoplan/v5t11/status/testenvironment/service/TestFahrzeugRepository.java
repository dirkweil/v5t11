package de.gedoplan.v5t11.status.testenvironment.service;

import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.status.entity.fahrzeug.FahrzeugId;
import de.gedoplan.v5t11.status.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.persistence.EntityManager;

@ApplicationScoped
@Alternative
@Priority(1)
public class TestFahrzeugRepository extends FahrzeugRepository {

  public static final Fahrzeug lok103_003_0 = new Fahrzeug(new FahrzeugId(SystemTyp.DCC, 1103));
  public static final Fahrzeug lok210_004_8 = new Fahrzeug(new FahrzeugId(SystemTyp.SX1, 2));
  public static final Fahrzeug lok217_001_7 = new Fahrzeug(new FahrzeugId(SystemTyp.SX2, 1217));

  private static final Fahrzeug[] fahrzeuge = { lok103_003_0, lok210_004_8, lok217_001_7 };

  @Override
  public List<Fahrzeug> findAll() {
    return Arrays.asList(fahrzeuge);
  }

  /*
   * Keinen EntityManager nutzen.
   * Die überschriebene Methode fordert einen EM per Injektion an, was im Testumfeld fehlschlägt.
   */
  @Override
  public void setEntityManager(EntityManager entityManager) {
  }

}

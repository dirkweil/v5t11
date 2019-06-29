package de.gedoplan.v5t11.status.testenvironment.service;

import de.gedoplan.v5t11.status.entity.SystemTyp;
import de.gedoplan.v5t11.status.entity.lok.Lok;
import de.gedoplan.v5t11.status.persistence.LokRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

@ApplicationScoped
@Specializes
public class TestLokRepository extends LokRepository {

  public static final Lok testLok103 = new Lok("103 003-0", SystemTyp.DCC, false, 1103, 126);
  static {
    testLok103.getFunktionConfigs().put(2, new Lok.FunktionConfig("Motorgeräusch", false));
    testLok103.getFunktionConfigs().put(7, new Lok.FunktionConfig("Pfiff", true));
  }

  public static final Lok testLok111 = new Lok("111 205-1", SystemTyp.SX, false, 2, 31);
  static {
    testLok111.getFunktionConfigs().put(1, new Lok.FunktionConfig("Horn", true, true));
  }

  public static final Lok testLok217 = new Lok("217 001-7", SystemTyp.SX2, false, 1217, 127);
  static {
    testLok217.getFunktionConfigs().put(2, new Lok.FunktionConfig("Motorgeräusch", false));
    testLok217.getFunktionConfigs().put(7, new Lok.FunktionConfig("Pfiff", true));
    testLok217.getFunktionConfigs().put(9, new Lok.FunktionConfig("Bahnsteigansage", true));
  }

  private ConcurrentMap<String, Lok> loks = new ConcurrentHashMap<>();

  @PostConstruct
  void postConstruct() {
    persist(testLok103);
    persist(testLok111);
    persist(testLok217);
  }

  @Override
  public List<Lok> findAll() {
    return new ArrayList<>(this.loks.values());
  }

  @Override
  public void persist(Lok lok) {
    if (this.loks.putIfAbsent(lok.getId(), lok) != null) {
      throw new EntityExistsException();
    }
  }

  @Override
  public Lok merge(Lok lok) {
    this.loks.put(lok.getId(), lok);
    return lok;
  }

  @Override
  public boolean removeById(String id) {
    return this.loks.remove(id) != null;
  }

  /*
   * Keinen EntityManager nutzen.
   * Die überschriebene Methode fordert einen EM per Injektion an, was im Testumfeld fehlschlägt.
   */
  @Override
  public void setEntityManager(EntityManager entityManager) {
  }

}

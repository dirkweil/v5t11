package de.gedoplan.v5t11.status.testenvironment.service;

import de.gedoplan.v5t11.status.entity.lok.Lok;
import de.gedoplan.v5t11.status.persistence.LokRepository;
import de.gedoplan.v5t11.status.service.init.TestLokData;

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

  private ConcurrentMap<String, Lok> loks = new ConcurrentHashMap<>();

  @PostConstruct
  void postConstruct() {
    for (Lok lok : TestLokData.loks) {
      persist(lok);
    }
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

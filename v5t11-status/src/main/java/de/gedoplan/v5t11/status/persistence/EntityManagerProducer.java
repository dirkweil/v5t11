package de.gedoplan.v5t11.status.persistence;

import javax.enterprise.context.ApplicationScoped;

// CHECKSTYLE:OFF

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@ApplicationScoped
public class EntityManagerProducer {
  @PersistenceContext
  @Produces
  private EntityManager entityManager;
}

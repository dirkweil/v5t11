package de.gedoplan.v5t11.betriebssteuerung.producer;

// CHECKSTYLE:OFF

import javax.enterprise.inject.Produces;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public class EntityManagerProducer
{
  @SuppressWarnings("unused")
  @PersistenceContext(name = "default")
  @Produces
  private EntityManager entityManager;
}

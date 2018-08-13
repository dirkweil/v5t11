package de.gedoplan.v5t11.status.testenvironment.service;

import de.gedoplan.v5t11.status.service.StatusPublisher;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;

@ApplicationScoped
@Specializes
public class TestStatusPublisher extends StatusPublisher {

  @Override
  public void publish(String category, Object status) {
  }

}

package de.gedoplan.v5t11.fahrstrassen.testenvironment;

import de.gedoplan.v5t11.fahrstrassen.service.FahrstrassePublisher;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;

@ApplicationScoped
@Specializes
public class TestFahrstrassePublisher extends FahrstrassePublisher {

  @Override
  protected void publish(String category, Object status) {
  }

}

package de.gedoplan.v5t11.leitstand.testenvironment;

import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.gateway.FahrstrasseResourceClient;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Specializes;
import javax.ws.rs.NotFoundException;

@Dependent
@Specializes
public class TestFahrstrasseResourceClient extends FahrstrasseResourceClient {

  @Override
  public Fahrstrasse getFahrstrasse(String bereich, String name) {
    throw new NotFoundException();
  }

}

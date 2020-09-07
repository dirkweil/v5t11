package de.gedoplan.v5t11.leitstand.testenvironment;

import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.leitstand.gateway.FahrstrassenGateway;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenFilter;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;

import java.util.List;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Alternative;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@Dependent
@RestClient
@Alternative
@Priority(1)
public class TestFahrstrassenGateway implements FahrstrassenGateway {

  @Override
  public Fahrstrasse getFahrstrasse(String bereich, String name) {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Fahrstrasse> getFahrstrassen(String startBereich, String startName, String endeBereich, String endeName, FahrstrassenFilter filter) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void reserviereFahrstrasse(String bereich, String name, FahrstrassenReservierungsTyp reservierungsTyp) {
    throw new UnsupportedOperationException();
  }

}

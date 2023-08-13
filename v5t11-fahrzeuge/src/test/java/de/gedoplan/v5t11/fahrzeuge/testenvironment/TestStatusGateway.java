package de.gedoplan.v5t11.fahrzeuge.testenvironment;

import de.gedoplan.v5t11.fahrzeuge.gateway.StatusGateway;
import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Alternative;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@Dependent
@RestClient
@Alternative
@Priority(1)
public class TestStatusGateway implements StatusGateway {

  @Override
  public void changeFahrzeug(FahrzeugId id, Boolean aktiv, Integer fahrstufe, Integer fktBits, Boolean licht, Boolean rueckwaerts) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setLokcontrollerAssignment(String id, FahrzeugId lokId, int hornBits) {
    throw new UnsupportedOperationException();
  }

}

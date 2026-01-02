package de.gedoplan.v5t11.fahrzeuge.gateway;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenFilter;

import java.util.Set;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "v5t11.parcours")
public interface ParcoursGateway {

  @GET
  @Path("fahrstrasse")
  @Produces(MediaType.APPLICATION_JSON)
  public Set<Fahrstrasse> getFahrstrassen(
    @QueryParam("filter") FahrstrassenFilter filter);

}

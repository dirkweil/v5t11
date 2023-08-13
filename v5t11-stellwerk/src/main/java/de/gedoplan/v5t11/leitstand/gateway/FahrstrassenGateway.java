package de.gedoplan.v5t11.leitstand.gateway;

import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenFilter;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;

import java.util.List;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "v5t11.fahrstrassen")
public interface FahrstrassenGateway {

  @GET
  @Path("fahrstrasse/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  Fahrstrasse getFahrstrasse(@PathParam("id") BereichselementId id);

  @GET
  @Path("fahrstrasse")
  @Produces(MediaType.APPLICATION_JSON)
  public List<Fahrstrasse> getFahrstrassen(
    @QueryParam("startBereich") String startBereich,
    @QueryParam("startName") String startName,
    @QueryParam("endeBereich") String endeBereich,
    @QueryParam("endeName") String endeName,
    @QueryParam("filter") FahrstrassenFilter filter);

  @PUT
  @Path("fahrstrasse/{id}/reservierung")
  @Consumes(MediaType.TEXT_PLAIN)
  void reserviereFahrstrasse(@PathParam("id") BereichselementId id, FahrstrassenReservierungsTyp reservierungsTyp);

}

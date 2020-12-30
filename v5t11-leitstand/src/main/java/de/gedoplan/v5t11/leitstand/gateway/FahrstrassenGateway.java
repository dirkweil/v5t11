package de.gedoplan.v5t11.leitstand.gateway;

import de.gedoplan.v5t11.leitstand.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenFilter;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

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

package de.gedoplan.v5t11.fahrzeuge.gateway;

import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;
import de.gedoplan.v5t11.util.domain.attribute.SystemTyp;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "v5t11.status")
public interface StatusGateway extends Serializable {
  // Fahrzeuge
  @PUT
  @Path("fahrzeug/{id}")
  @Consumes(MediaType.MEDIA_TYPE_WILDCARD)
  public void changeFahrzeug(
    @PathParam("id") FahrzeugId id,
    @QueryParam("aktiv") Boolean aktiv,
    @QueryParam("fahrstufe") Integer fahrstufe,
    @QueryParam("fktBits") Integer fktBits,
    @QueryParam("licht") Boolean licht,
    @QueryParam("rueckwaerts") Boolean rueckwaerts);

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("fahrzeug/config/{systemTyp}")
  public Map<Integer, Integer> getFahrzeugConfig(@PathParam("systemTyp") SystemTyp systemTyp, @QueryParam("key") List<Integer> keys);

  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Path("fahrzeug/config/{systemTyp}")
  public void setFahrzeugConfig(@PathParam("systemTyp") SystemTyp systemTyp, Map<Integer, Integer> fahrzeugConfigParameters);

  // Lokcontroller
  @PUT
  @Path("lokcontroller/{id}")
  @Consumes(MediaType.MEDIA_TYPE_WILDCARD)
  public void setLokcontrollerAssignment(@PathParam("id") String id, @QueryParam("lokId") FahrzeugId lokId, @QueryParam("hornBits") int hornBits);

}

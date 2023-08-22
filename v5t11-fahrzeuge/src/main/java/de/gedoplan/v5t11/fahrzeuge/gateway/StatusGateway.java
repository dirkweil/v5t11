package de.gedoplan.v5t11.fahrzeuge.gateway;

import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;

import java.io.Serializable;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
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

  // Lokcontroller
  @PUT
  @Path("lokcontroller/{id}")
  @Consumes(MediaType.MEDIA_TYPE_WILDCARD)
  public void setLokcontrollerAssignment(@PathParam("id") String id, @QueryParam("lokId") FahrzeugId lokId, @QueryParam("hornBits") int hornBits);

}

package de.gedoplan.v5t11.fahrzeuge.gateway;

import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;

import javax.ws.rs.Consumes;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "v5t11.status")
public interface StatusGateway {
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

}

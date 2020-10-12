package de.gedoplan.v5t11.fahrzeuge.gateway;

import de.gedoplan.v5t11.fahrzeuge.entity.baustein.Zentrale;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Weiche;

import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "v5t11.status")
public interface StatusGateway {
  // Gleise
  @GET
  @Path("gleis")
  @Produces(MediaType.APPLICATION_JSON)
  Set<Gleisabschnitt> getGleisabschnitte();

  // Weichen
  @GET
  @Path("weiche")
  @Produces(MediaType.APPLICATION_JSON)
  Set<Weiche> getWeichen();

  // Zentrale
  @GET
  @Path("zentrale")
  @Produces(MediaType.APPLICATION_JSON)
  Zentrale getZentrale();

}

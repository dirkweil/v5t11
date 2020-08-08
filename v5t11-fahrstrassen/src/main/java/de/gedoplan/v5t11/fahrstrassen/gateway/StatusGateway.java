package de.gedoplan.v5t11.fahrstrassen.gateway;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;

import java.util.Collection;
import java.util.Set;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "v5t11.status")
public interface StatusGateway {
  @GET
  @Path("gleis")
  @Produces(MediaType.APPLICATION_JSON)
  Set<Gleisabschnitt> getGleisabschnitte();

  @PUT
  @Path("signal/{bereich}/{name}")
  @Consumes(MediaType.TEXT_PLAIN)
  void signalStellen(@PathParam("bereich") String bereich, @PathParam("name") String name, SignalStellung stellung);

  @PUT
  @Path("signal/{bereich}/{name}")
  @Consumes(MediaType.TEXT_PLAIN)
  void signalStellen(@PathParam("bereich") String bereich, @PathParam("name") String name, Collection<SignalStellung> stellungen);

  @PUT
  @Path("weiche/{bereich}/{name}")
  @Consumes(MediaType.TEXT_PLAIN)
  void weicheStellen(@PathParam("bereich") String bereich, @PathParam("name") String name, WeichenStellung stellung);
}

package de.gedoplan.v5t11.fahrstrassen.gateway;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;

import java.util.Collection;
import java.util.Set;

import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@RegisterRestClient(configKey = "v5t11.status")
public interface StatusGateway {
  @GET
  @Path("gleis")
  @Produces(MediaType.APPLICATION_JSON)
  Set<Gleis> getGleise();

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

package de.gedoplan.v5t11.leitstand.gateway;

import de.gedoplan.v5t11.leitstand.entity.baustein.Zentrale;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Signal;
import de.gedoplan.v5t11.leitstand.entity.fahrweg.Weiche;
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
  // Gleise
  @GET
  @Path("gleis")
  @Produces(MediaType.APPLICATION_JSON)
  Set<Gleis> getGleise();

  // Signale
  @GET
  @Path("signal")
  @Produces(MediaType.APPLICATION_JSON)
  Set<Signal> getSignale();

  @PUT
  @Path("signal/{bereich}/{name}")
  @Consumes(MediaType.TEXT_PLAIN)
  void signalStellen(@PathParam("bereich") String bereich, @PathParam("name") String name, SignalStellung stellung);

  @PUT
  @Path("signal/{bereich}/{name}")
  @Consumes(MediaType.TEXT_PLAIN)
  void signalStellen(@PathParam("bereich") String bereich, @PathParam("name") String name, Collection<SignalStellung> stellungen);

  // Weichen
  @GET
  @Path("weiche")
  @Produces(MediaType.APPLICATION_JSON)
  Set<Weiche> getWeichen();

  @PUT
  @Path("weiche/{bereich}/{name}")
  @Consumes(MediaType.TEXT_PLAIN)
  void weicheStellen(@PathParam("bereich") String bereich, @PathParam("name") String name, WeichenStellung stellung);

  // Zentrale
  @GET
  @Path("zentrale")
  @Produces(MediaType.APPLICATION_JSON)
  Zentrale getZentrale();

  @PUT
  @Consumes(MediaType.TEXT_PLAIN)
  @Path("zentrale")
  void putGleisspannung(String on);

}

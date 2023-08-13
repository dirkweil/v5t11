package de.gedoplan.v5t11.status.webservice;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleis;

import java.util.Set;
import java.util.SortedSet;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("gleis")
@Dependent
public class GleisResource {

  @Inject
  Steuerung steuerung;

  @GET
  @Path("{bereich}/{name}")
  @Produces(MediaType.TEXT_PLAIN + "; qs=1.0")
  public boolean getGleissBelegung(@PathParam("bereich") String bereich, @PathParam("name") String name) {

    return getGleis(bereich, name).isBesetzt();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public SortedSet<Gleis> getGleise() {

    return this.steuerung.getGleise();
  }

  @GET
  @Path("{bereich}")
  @Produces(MediaType.APPLICATION_JSON)
  public Set<Gleis> getGleise(@PathParam("bereich") String bereich) {

    if (!this.steuerung.getBereiche().contains(bereich)) {
      throw new NotFoundException();
    }

    return this.steuerung.getGleise(bereich);
  }

  @GET
  @Path("{bereich}/{name}")
  @Produces(MediaType.APPLICATION_JSON + "; qs=0.7")
  public Gleis getGleis(@PathParam("bereich") String bereich, @PathParam("name") String name) {

    Gleis gleis = this.steuerung.getGleis(bereich, name);
    if (gleis == null) {
      throw new NotFoundException();
    }

    return gleis;
  }
}

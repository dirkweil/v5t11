package de.gedoplan.v5t11.status.webservice;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;

import java.util.Set;
import java.util.SortedSet;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("gleis")
@Dependent
public class GleisResource {

  @Inject
  Steuerung steuerung;

  @GET
  @Path("{bereich}/{name}")
  @Produces(MediaType.TEXT_PLAIN + "; qs=1.0")
  public boolean getGleisabschnittsBelegung(@PathParam("bereich") String bereich, @PathParam("name") String name) {

    return getGleisabschnitt(bereich, name).isBesetzt();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public SortedSet<Gleisabschnitt> getGleisabschnitte() {

    return this.steuerung.getGleisabschnitte();
  }

  @GET
  @Path("{bereich}")
  @Produces(MediaType.APPLICATION_JSON)
  public Set<Gleisabschnitt> getGleisabschnitte(@PathParam("bereich") String bereich) {

    Jsonb jsonb = JsonbBuilder.create();
    System.out.println(jsonb);

    if (!this.steuerung.getBereiche().contains(bereich)) {
      throw new NotFoundException();
    }

    return this.steuerung.getGleisabschnitte(bereich);
  }

  @GET
  @Path("{bereich}/{name}")
  @Produces(MediaType.APPLICATION_JSON + "; qs=0.7")
  public Gleisabschnitt getGleisabschnitt(@PathParam("bereich") String bereich, @PathParam("name") String name) {

    Gleisabschnitt gleisabschnitt = this.steuerung.getGleisabschnitt(bereich, name);
    if (gleisabschnitt == null) {
      throw new NotFoundException();
    }

    return gleisabschnitt;
  }
}

package de.gedoplan.v5t11.status.webservice;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Lokcontroller;
import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;

import java.util.SortedSet;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("lokcontroller")
@Dependent
public class LokcontrollerEndpoint {

  @Inject
  Steuerung steuerung;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public SortedSet<Lokcontroller> getLokcontroller() {

    return this.steuerung.getLokcontroller();
  }

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Lokcontroller getLokcontroller(@PathParam("id") String id) {

    Lokcontroller lokcontroller = this.steuerung.getLokcontroller(id);
    if (lokcontroller == null) {
      throw new NotFoundException();
    }

    return lokcontroller;
  }

  @PUT
  @Path("{id}")
  @Consumes(MediaType.APPLICATION_JSON)
  public void setLok(@PathParam("id") String id, FahrzeugId lokId) {

    if (lokId != null && (lokId.getSystemTyp() == null || lokId.getAdresse() == 0)) {
      lokId = null;
    }

    try {
      this.steuerung.assignLokcontroller(id, lokId);
    } catch (IllegalArgumentException e) {
      throw new NotFoundException();
    }
  }
}

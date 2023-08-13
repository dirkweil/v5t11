package de.gedoplan.v5t11.status.webservice;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Lokcontroller;
import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;

import java.util.SortedSet;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("lokcontroller")
@Dependent
public class LokcontrollerResource {

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
  @Consumes(MediaType.MEDIA_TYPE_WILDCARD)
  public void setLok(@PathParam("id") String id, @QueryParam("lokId") FahrzeugId lokId, @QueryParam("hornBits") int hornBits) {

    if (lokId != null && (lokId.getSystemTyp() == null || lokId.getAdresse() == 0)) {
      lokId = null;
    }

    try {
      this.steuerung.assignLokcontroller(id, lokId, hornBits);
    } catch (IllegalArgumentException e) {
      throw new NotFoundException();
    }
  }
}

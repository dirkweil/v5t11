package de.gedoplan.v5t11.status.webservice;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.lok.Lok;

import java.util.SortedSet;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("lok")
@Dependent
public class LokResource {

  @Inject
  Steuerung steuerung;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public SortedSet<Lok> getLoks() {

    return this.steuerung.getLoks();
  }

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Lok getLok(@PathParam("id") String id) {

    Lok lok = this.steuerung.getLok(id);
    if (lok == null) {
      throw new NotFoundException();
    }

    return lok;
  }
}

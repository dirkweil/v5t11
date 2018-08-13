package de.gedoplan.v5t11.status.webservice;

import de.gedoplan.v5t11.status.entity.Steuerung;

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

@Path("kanal")
@Dependent
public class KanalResource {

  @Inject
  Steuerung steuerung;

  @GET
  @Path("{adr}")
  @Produces(MediaType.TEXT_PLAIN)
  public int getKanalWert(@PathParam("adr") int adr) {
    if (adr < 0 || adr > 255) {
      throw new NotFoundException();
    }

    return this.steuerung.getKanalWert(adr);
  }

  @PUT
  @Path("{adr}")
  @Consumes(MediaType.TEXT_PLAIN)
  public void putKanalWert(@PathParam("adr") int adr, int wert) {
    if (adr < 0 || adr > 255) {
      throw new NotFoundException();
    }

    this.steuerung.setKanalWert(adr, wert);
  }
}

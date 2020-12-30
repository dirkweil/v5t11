package de.gedoplan.v5t11.status.webservice;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("zentrale")
@Dependent
public class ZentraleEndpoint {

  @Inject
  Steuerung steuerung;

  @GET
  @Produces(MediaType.TEXT_PLAIN + "; qs=1.0")
  public boolean getGleisspannung() {

    return this.steuerung.getZentrale().isGleisspannung();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON + "; qs=0.7")
  public Zentrale getZentrale() {

    return this.steuerung.getZentrale();
  }

  @PUT
  @Consumes("*/*")
  public void putGleisspannung(String aktivAsString) {

    this.steuerung.getZentrale().setGleisspannung(aktivAsString.startsWith("t"));
  }
}

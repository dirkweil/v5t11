package de.gedoplan.v5t11.status.webservice;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.baustein.Zentrale;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("zentrale")
@Dependent
public class ZentraleResource {

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

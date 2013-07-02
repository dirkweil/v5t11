package de.gedoplan.v5t11.betriebssteuerung.webservice;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("steuerung/zentrale")
public class ZentraleWebservice
{
  @Inject
  private Steuerung steuerung;

  @Path("aktiv")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public boolean isAktiv()
  {
    return this.steuerung.getZentrale().isAktiv();
  }

  @Path("aktiv")
  @POST
  public Response setAktiv(@FormParam("aktiv") boolean aktiv)
  {
    this.steuerung.getZentrale().setAktiv(aktiv);
    return Response.ok().build();
  }

}

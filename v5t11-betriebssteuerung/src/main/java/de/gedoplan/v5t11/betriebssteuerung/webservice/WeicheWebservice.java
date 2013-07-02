package de.gedoplan.v5t11.betriebssteuerung.webservice;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Weiche.Stellung;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

@Path("steuerung/weiche")
public class WeicheWebservice
{
  @Inject
  private Steuerung steuerung;

  @Path("{bereich}/{name}/stellung")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Stellung getStellung(@PathParam("bereich") String bereich, @PathParam("name") String name)
  {
    Weiche weiche = this.steuerung.getWeiche(bereich, name);
    if (weiche == null)
    {
      // REVIEW: Kann man hier einen HTTP-Status 404 liefern?
      return null;
    }

    return weiche.getStellung();
  }

  @Path("{bereich}/{name}/stellung")
  @POST
  public Response setStellung(@PathParam("bereich") String bereich, @PathParam("name") String name, @FormParam("stellung") Stellung stellung)
  {
    Weiche weiche = this.steuerung.getWeiche(bereich, name);
    if (weiche == null)
    {
      return Response.status(Status.NOT_FOUND).build();
    }

    weiche.setStellung(stellung);
    return Response.ok().build();
  }
}

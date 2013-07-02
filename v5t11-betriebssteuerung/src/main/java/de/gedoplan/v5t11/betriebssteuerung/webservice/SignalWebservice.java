package de.gedoplan.v5t11.betriebssteuerung.webservice;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrweg.geraet.Signal.Stellung;

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

@Path("steuerung/signal")
public class SignalWebservice
{
  @Inject
  private Steuerung steuerung;

  @Path("{bereich}/{name}/stellung")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Stellung getStellung(@PathParam("bereich") String bereich, @PathParam("name") String name)
  {
    Signal signal = this.steuerung.getSignal(bereich, name);
    if (signal == null)
    {
      // REVIEW: Kann man hier einen HTTP-Status 404 liefern?
      return null;
    }

    return signal.getStellung();
  }

  @Path("{bereich}/{name}/stellung")
  @POST
  public Response setStellung(@PathParam("bereich") String bereich, @PathParam("name") String name, @FormParam("stellung") Stellung stellung)
  {
    Signal signal = this.steuerung.getSignal(bereich, name);
    if (signal == null)
    {
      return Response.status(Status.NOT_FOUND).build();
    }

    signal.setStellung(stellung);
    return Response.ok().build();
  }
}

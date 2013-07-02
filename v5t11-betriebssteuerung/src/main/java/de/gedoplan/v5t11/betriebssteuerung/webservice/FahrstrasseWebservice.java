package de.gedoplan.v5t11.betriebssteuerung.webservice;

import de.gedoplan.v5t11.betriebssteuerung.background.FahrstrassenSteuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.fahrstrasse.Fahrstrasse.ReservierungsTyp;

import javax.inject.Inject;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

/**
 * REST-Webservice für Fahrstrassen.
 * 
 * @author dw
 */
@Path("steuerung/fahrstrasse")
public class FahrstrasseWebservice
{
  @Inject
  private FahrstrassenSteuerung fahrstrassenSteuerung;

  /**
   * Fahrstrasse reservieren bzw. freigeben.
   * 
   * @param bereich Bereich
   * @param name Name
   * @param reservierungsTyp <code>null</code> zum Freigeben, sonst Art der Fahrstrassenreservierung
   * @return Response
   */
  @Path("{bereich}/{name}/reserviert")
  @POST
  public Response setReserviert(@PathParam("bereich") String bereich, @PathParam("name") String name, @FormParam("reservierungsTyp") ReservierungsTyp reservierungsTyp)
  {
    this.fahrstrassenSteuerung.reserviereFahrstrasse(bereich, name, reservierungsTyp);
    return Response.ok().build();
  }
}

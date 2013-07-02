package de.gedoplan.v5t11.betriebssteuerung.webservice;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("steuerung")
public class SteuerungWebservice
{
  @Inject
  Steuerung steuerung;

  @GET
  @Produces({ MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON })
  public Steuerung getSteuerung()
  {
    return this.steuerung;
  }
}

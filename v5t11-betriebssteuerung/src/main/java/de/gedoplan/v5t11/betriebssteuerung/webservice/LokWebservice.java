package de.gedoplan.v5t11.betriebssteuerung.webservice;

import de.gedoplan.v5t11.betriebssteuerung.steuerung.Steuerung;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.lok.Lok;
import de.gedoplan.v5t11.betriebssteuerung.steuerung.lok.LokSet;

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

@Path("steuerung/lok")
public class LokWebservice
{
  @Inject
  private Steuerung steuerung;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public LokSet getLoks()
  {
    LokSet lokSet = new LokSet();

    lokSet.setLokset(this.steuerung.getLoks());

    return lokSet;
  }

  @Path("{adr}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Lok getLok(@PathParam("adr") int adresse)
  {
    return this.steuerung.getLok(adresse);
  }

  @Path("{adr}/horn")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public boolean getHorn(@PathParam("adr") int adresse)
  {
    Lok lok = this.steuerung.getLok(adresse);
    if (lok == null)
    {
      // REVIEW: Kann man hier einen HTTP-Status 404 liefern?
      return false;
    }

    return lok.isHorn();
  }

  @Path("{adr}/horn")
  @POST
  public Response setHorn(@PathParam("adr") int adresse, @FormParam("horn") boolean horn)
  {
    Lok lok = this.steuerung.getLok(adresse);
    if (lok == null)
    {
      return Response.status(Status.NOT_FOUND).build();
    }

    lok.setHorn(horn);
    return Response.ok().build();
  }

  @Path("{adr}/licht")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public boolean getLicht(@PathParam("adr") int adresse)
  {
    Lok lok = this.steuerung.getLok(adresse);
    if (lok == null)
    {
      // REVIEW: Kann man hier einen HTTP-Status 404 liefern?
      return false;
    }

    return lok.isLicht();
  }

  @Path("{adr}/licht")
  @POST
  public Response setLicht(@PathParam("adr") int adresse, @FormParam("licht") boolean licht)
  {
    Lok lok = this.steuerung.getLok(adresse);
    if (lok == null)
    {
      return Response.status(Status.NOT_FOUND).build();
    }

    lok.setLicht(licht);
    return Response.ok().build();
  }

  @Path("{adr}/rueckwaerts")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public boolean getRueckwaerts(@PathParam("adr") int adresse)
  {
    Lok lok = this.steuerung.getLok(adresse);
    if (lok == null)
    {
      // REVIEW: Kann man hier einen HTTP-Status 404 liefern?
      return false;
    }

    return lok.isRueckwaerts();
  }

  @Path("{adr}/rueckwaerts")
  @POST
  public Response setRueckwaerts(@PathParam("adr") int adresse, @FormParam("rueckwaerts") boolean rueckwaerts)
  {
    Lok lok = this.steuerung.getLok(adresse);
    if (lok == null)
    {
      return Response.status(Status.NOT_FOUND).build();
    }

    lok.setRueckwaerts(rueckwaerts);
    return Response.ok().build();
  }

  @Path("{adr}/geschwindigkeit")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public int getGeschwindigkeit(@PathParam("adr") int adresse)
  {
    Lok lok = this.steuerung.getLok(adresse);
    if (lok == null)
    {
      // REVIEW: Kann man hier einen HTTP-Status 404 liefern?
      return 0;
    }

    return lok.getGeschwindigkeit();
  }

  @Path("{adr}/geschwindigkeit")
  @POST
  public Response setGeschwindigkeit(@PathParam("adr") int adresse, @FormParam("geschwindigkeit") int geschwindigkeit)
  {
    Lok lok = this.steuerung.getLok(adresse);
    if (lok == null)
    {
      return Response.status(Status.NOT_FOUND).build();
    }

    lok.setGeschwindigkeit(geschwindigkeit);
    return Response.ok().build();
  }
}

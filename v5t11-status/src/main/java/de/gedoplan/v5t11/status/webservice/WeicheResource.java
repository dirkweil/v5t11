package de.gedoplan.v5t11.status.webservice;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;

import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.BadRequestException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("weiche")
@Dependent
public class WeicheResource {

  @Inject
  Steuerung steuerung;

  @GET
  @Path("{bereich}/{name}")
  @Produces(MediaType.TEXT_PLAIN + "; qs=1.0")
  public WeichenStellung getWeichenStellung(@PathParam("bereich") String bereich, @PathParam("name") String name) {

    return getWeiche(bereich, name).getStellung();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Set<Weiche> getWeichen() {

    return this.steuerung.getWeichen();
  }

  @GET
  @Path("{bereich}")
  @Produces(MediaType.APPLICATION_JSON)
  public Set<Weiche> getWeichen(@PathParam("bereich") String bereich) {

    if (!this.steuerung.getBereiche().contains(bereich)) {
      throw new NotFoundException();
    }

    return this.steuerung.getWeichen(bereich);
  }

  @GET
  @Path("{bereich}/{name}")
  @Produces(MediaType.APPLICATION_JSON + "; qs=0.7")
  public Weiche getWeiche(@PathParam("bereich") String bereich, @PathParam("name") String name) {

    Weiche weiche = this.steuerung.getWeiche(bereich, name);
    if (weiche == null) {
      throw new NotFoundException();
    }

    return weiche;
  }

  @PUT
  @Path("{bereich}/{name}")
  @Consumes(MediaType.TEXT_PLAIN)
  public void putWeichenStellung(@PathParam("bereich") String bereich, @PathParam("name") String name, String stellungsName) {

    WeichenStellung stellung = WeichenStellung.valueOf(stellungsName);
    if (stellung == null) {
      throw new BadRequestException();
    }

    getWeiche(bereich, name).setStellung(stellung);
  }
}

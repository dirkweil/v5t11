package de.gedoplan.v5t11.status.webservice;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.util.domain.attribute.WeichenStellung;

import java.util.Set;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

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
  @Consumes(MediaType.MEDIA_TYPE_WILDCARD)
  public void putWeichenStellung(@PathParam("bereich") String bereich, @PathParam("name") String name, String stellungsName) {

    try {
      getWeiche(bereich, name).setStellung(WeichenStellung.fromString(stellungsName));
    } catch (IllegalArgumentException e) {
      throw new BadRequestException();
    }
  }
}

package de.gedoplan.v5t11.status.webservice;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;

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

@Path("signal")
@Dependent
public class SignalEndpoint {

  @Inject
  Steuerung steuerung;

  @GET
  @Path("{bereich}/{name}")
  @Produces(MediaType.TEXT_PLAIN + "; qs=1.0")
  public SignalStellung getSignalStellung(@PathParam("bereich") String bereich, @PathParam("name") String name) {

    return getSignal(bereich, name).getStellung();
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Set<Signal> getSignale() {

    return this.steuerung.getSignale();
  }

  @GET
  @Path("{bereich}")
  @Produces(MediaType.APPLICATION_JSON)
  public Set<Signal> getSignale(@PathParam("bereich") String bereich) {

    if (!this.steuerung.getBereiche().contains(bereich)) {
      throw new NotFoundException();
    }

    return this.steuerung.getSignale(bereich);
  }

  @GET
  @Path("{bereich}/{name}")
  @Produces(MediaType.APPLICATION_JSON + "; qs=0.7")
  public Signal getSignal(@PathParam("bereich") String bereich, @PathParam("name") String name) {

    Signal signal = this.steuerung.getSignal(bereich, name);
    if (signal == null) {
      throw new NotFoundException();
    }

    return signal;
  }

  @PUT
  @Path("{bereich}/{name}")
  @Consumes(MediaType.MEDIA_TYPE_WILDCARD)
  public void putSignalStellung(@PathParam("bereich") String bereich, @PathParam("name") String name, String stellungsAngabe) {

    Signal signal = getSignal(bereich, name);
    if (signal == null) {
      throw new NotFoundException();
    }

    if (stellungsAngabe.startsWith("[") && stellungsAngabe.endsWith("]")) {
      stellungsAngabe = stellungsAngabe.substring(1, stellungsAngabe.length() - 1);
    }

    try {
      for (String stellungsName : stellungsAngabe.split("\\s*,\\s*")) {

        SignalStellung stellung = SignalStellung.ofCode(stellungsName);

        if (signal.getTyp().getErlaubteStellungen().contains(stellung)) {
          signal.setStellung(stellung);
          return;
        }
      }
    } catch (IllegalArgumentException e) {
      // Fall through
    }

    throw new BadRequestException();
  }
}

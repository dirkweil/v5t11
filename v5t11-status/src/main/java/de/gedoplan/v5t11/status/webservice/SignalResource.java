package de.gedoplan.v5t11.status.webservice;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.util.domain.attribute.SignalStellung;

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

@Path("signal")
@Dependent
public class SignalResource {

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

        SignalStellung stellung = SignalStellung.fromString(stellungsName);

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

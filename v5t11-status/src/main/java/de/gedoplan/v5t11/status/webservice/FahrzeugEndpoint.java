package de.gedoplan.v5t11.status.webservice;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;

import java.util.Collection;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

@Path("fahrzeug")
@Dependent
public class FahrzeugEndpoint {

  @Inject
  Steuerung steuerung;

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Collection<Fahrzeug> getAll() {

    return this.steuerung.getFahrzeuge();
  }

  @PUT
  @Path("{id}")
  @Consumes(MediaType.MEDIA_TYPE_WILDCARD)
  public void change(
      @PathParam("id") FahrzeugId id,
      @QueryParam("aktiv") Boolean aktiv,
      @QueryParam("fahrstufe") Integer fahrstufe,
      @QueryParam("fktBits") Integer fktBits,
      @QueryParam("licht") Boolean licht,
      @QueryParam("rueckwaerts") Boolean rueckwaerts) {

    Fahrzeug fahrzeug = this.steuerung.getOrCreateFahrzeug(id);
    if (aktiv != null) {
      fahrzeug.setAktiv(aktiv);
    }
    if (fahrstufe != null) {
      fahrzeug.setFahrstufe(fahrstufe);
    }
    if (fktBits != null) {
      fahrzeug.setFktBits(fktBits);
    }
    if (licht != null) {
      fahrzeug.setLicht(licht);
    }
    if (rueckwaerts != null) {
      fahrzeug.setRueckwaerts(rueckwaerts);
    }

  }

}

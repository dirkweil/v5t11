package de.gedoplan.v5t11.status.webservice;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.domain.attribute.FahrzeugId;

import java.util.Collection;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

@Path("fahrzeug")
@Dependent
public class FahrzeugResource {

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

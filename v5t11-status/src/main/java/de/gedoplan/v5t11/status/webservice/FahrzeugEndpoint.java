package de.gedoplan.v5t11.status.webservice;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;

import java.util.Collection;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
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

}

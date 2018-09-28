package de.gedoplan.v5t11.fahrstrassen.webservice;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;
import de.gedoplan.v5t11.util.webservice.ResponseFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.logging.Log;

@Path("fahrstrasse")
@ApplicationScoped
public class FahrstrasseResource {

  @Inject
  Parcours parcours;

  @Inject
  Log log;

  @GET
  @Path("{bereich}/{name}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getFahrstrasse(@PathParam("bereich") String bereich, @PathParam("name") String name) {
    Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse(bereich, name);
    if (fahrstrasse == null) {
      return ResponseFactory.createNotFoundResponse();
    }

    return ResponseFactory.createJsonResponse(fahrstrasse, JsonbWithIncludeVisibility.FULL);
  }

  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response getFahrstrassen(
      @QueryParam("startBereich") String startBereich,
      @QueryParam("startName") String startName,
      @QueryParam("endeBereich") String endeBereich,
      @QueryParam("endeName") String endeName,
      @QueryParam("frei") boolean frei) {

    if (this.log.isDebugEnabled()) {
      this.log.debug(String.format("getFahrstrassen: start=%s/%s, ende=%s/%s, frei=%b", startBereich, startName, endeBereich, endeName, frei));
    }

    Gleisabschnitt start = null;
    if (startBereich != null || startName != null) {
      if (startBereich == null) {
        startBereich = endeBereich;
      }

      start = this.parcours.getGleisabschnitt(startBereich, startName);
      if (start == null) {
        return ResponseFactory.createNotFoundResponse();
      }
    }

    Gleisabschnitt ende = null;
    if (endeBereich != null || endeName != null) {
      if (endeName != null) {
        endeBereich = startBereich;
      }

      ende = this.parcours.getGleisabschnitt(endeBereich, endeName);
      if (ende == null) {
        return ResponseFactory.createNotFoundResponse();
      }
    }

    return ResponseFactory.createJsonResponse(this.parcours.getFahrstrassen(start, ende, frei), JsonbWithIncludeVisibility.FULL);
  }

  @PUT
  @Path("{bereich}/{name}/reservierung")
  @Consumes(MediaType.TEXT_PLAIN)
  public Response reserviereFahrstrasse(@PathParam("bereich") String bereich, @PathParam("name") String name, FahrstrassenReservierungsTyp reservierungsTyp) {
    if (this.log.isDebugEnabled()) {
      this.log.debug(String.format("reserviereFahrstrasse: fahrstrasse=%s/%s, reservierungsTyp=%s", bereich, name, reservierungsTyp));
    }

    Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse(bereich, name);
    if (fahrstrasse == null) {
      return ResponseFactory.createNotFoundResponse();
    }

    if (!fahrstrasse.reservieren(reservierungsTyp)) {
      return ResponseFactory.createConflictResponse();
    }

    return ResponseFactory.createNoContentResponse();
  }
}

package de.gedoplan.v5t11.fahrstrassen.webservice;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;
import de.gedoplan.v5t11.util.webservice.ResponseFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
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
  @Produces(MediaType.APPLICATION_JSON)
  public Response getFahrstrassen(
      @QueryParam("startBereich") String startBereich,
      @QueryParam("startName") String startName,
      @QueryParam("endeBereich") String endeBereich,
      @QueryParam("endeName") String endeName,
      @QueryParam("frei") boolean frei) {

    if (this.log.isDebugEnabled()) {
      this.log.debug(String.format("start=%s/%s, ende=%s/%s, frei=%b", startBereich, startName, endeBereich, endeName, frei));
    }

    Gleisabschnitt start = null;
    if (startBereich != null || startName != null) {
      if (startBereich == null) {
        startBereich = endeBereich;
      }

      start = this.parcours.getGleisabschnitt(startBereich, startName);
      if (start == null) {
        return ResponseFactory.createNotFoundReponse();
      }
    }

    Gleisabschnitt ende = null;
    if (endeBereich != null || endeName != null) {
      if (endeName != null) {
        endeBereich = startBereich;
      }

      ende = this.parcours.getGleisabschnitt(endeBereich, endeName);
      if (ende == null) {
        return ResponseFactory.createNotFoundReponse();
      }
    }

    return ResponseFactory.createJsonResponse(this.parcours.getFahrstrassen(start, ende, frei), JsonbWithIncludeVisibility.SHORT);
  }
}

package de.gedoplan.v5t11.fahrstrassen.webservice;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenFilter;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;
import de.gedoplan.v5t11.util.webservice.ResponseFactory;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
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
public class FahrstrasseEndpoint {

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
  @Produces(MediaType.APPLICATION_JSON + "; qs=1.0")
  public Response getFahrstrassenAsJson(
      @QueryParam("startBereich") String startBereich,
      @QueryParam("startName") String startName,
      @QueryParam("endeBereich") String endeBereich,
      @QueryParam("endeName") String endeName,
      @QueryParam("filter") String filterAsString) {

    List<Fahrstrasse> fahrstrassen = getFahrstrassen(startBereich, startName, endeBereich, endeName, filterAsString);
    return fahrstrassen != null
        ? ResponseFactory.createJsonResponse(fahrstrassen, JsonbWithIncludeVisibility.FULL)
        : ResponseFactory.createNotFoundResponse();
  }

  @GET
  @Produces(MediaType.TEXT_PLAIN + "; qs=0.7")
  public List<String> getFahrstrassenIds(
      @QueryParam("startBereich") String startBereich,
      @QueryParam("startName") String startName,
      @QueryParam("endeBereich") String endeBereich,
      @QueryParam("endeName") String endeName,
      @QueryParam("filter") String filterAsString) {

    List<Fahrstrasse> fahrstrassen = getFahrstrassen(startBereich, startName, endeBereich, endeName, filterAsString);
    if (fahrstrassen == null) {
      throw new NotFoundException();
    }

    return fahrstrassen.stream().map(fs -> fs.getId().getBereich() + "/" + fs.getId().getName()).collect(Collectors.toList());
  }

  private List<Fahrstrasse> getFahrstrassen(
      String startBereich,
      String startName,
      String endeBereich,
      String endeName,
      String filterAsString) {

    FahrstrassenFilter filter = filterAsString != null ? FahrstrassenFilter.valueOfLenient(filterAsString) : null;

    if (this.log.isDebugEnabled()) {
      this.log.debug(String.format("getFahrstrassen: start=%s/%s, ende=%s/%s, filter=%s", startBereich, startName, endeBereich, endeName, filter));
    }

    Gleisabschnitt start = null;
    if (startBereich != null || startName != null) {
      if (startBereich == null) {
        startBereich = endeBereich;
      }

      start = this.parcours.getGleisabschnitt(startBereich, startName);
      if (start == null) {
        return null;
      }
    }

    Gleisabschnitt ende = null;
    if (endeBereich != null || endeName != null) {
      if (endeBereich == null) {
        endeBereich = startBereich;
      }

      ende = this.parcours.getGleisabschnitt(endeBereich, endeName);
      if (ende == null) {
        return null;
      }
    }

    return this.parcours.getFahrstrassen(start, ende, filter);
  }

  @PUT
  @Path("{bereich}/{name}/reservierung")
  @Consumes(MediaType.TEXT_PLAIN)
  public Response reserviereFahrstrasse(@PathParam("bereich") String bereich, @PathParam("name") String name, String reservierungsTypAsString) {
    if (this.log.isDebugEnabled()) {
      this.log.debug(String.format("reserviereFahrstrasse: fahrstrasse=%s/%s, reservierungsTyp=%s", bereich, name, reservierungsTypAsString));
    }

    Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse(bereich, name);
    if (fahrstrasse == null) {
      return ResponseFactory.createNotFoundResponse();
    }

    try {
      if (!fahrstrasse.reservieren(FahrstrassenReservierungsTyp.valueOfLenient(reservierungsTypAsString))) {
        return ResponseFactory.createConflictResponse();
      }
    } catch (IllegalArgumentException e) {
      return ResponseFactory.createBadRequestResponse();
    }

    return ResponseFactory.createNoContentResponse();
  }
}

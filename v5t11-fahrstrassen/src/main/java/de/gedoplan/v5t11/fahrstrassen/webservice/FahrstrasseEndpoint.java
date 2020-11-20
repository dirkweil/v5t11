package de.gedoplan.v5t11.fahrstrassen.webservice;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.persistence.FahrstrassenStatusRepository;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenFilter;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;
import de.gedoplan.v5t11.util.webservice.ResponseFactory;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;
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

import org.jboss.logging.Logger;

@Path("fahrstrasse")
@ApplicationScoped
@Transactional(rollbackOn = Exception.class)
public class FahrstrasseEndpoint {

  @Inject
  Parcours parcours;

  @Inject
  FahrstrassenStatusRepository fahrstrassenStatusRepository;

  @Inject
  Logger log;

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

    FahrstrassenFilter filter = filterAsString != null ? FahrstrassenFilter.ofCode(filterAsString) : null;

    if (this.log.isDebugEnabled()) {
      this.log.debug(String.format("getFahrstrassen: start=%s/%s, ende=%s/%s, filter=%s", startBereich, startName, endeBereich, endeName, filter));
    }

    BereichselementId startId = null;
    if (startBereich != null || startName != null) {
      if (startBereich == null) {
        startBereich = endeBereich;
      }
      startId = new BereichselementId(startBereich, startName);
    }

    BereichselementId endeId = null;
    if (endeBereich != null || endeName != null) {
      if (endeBereich == null) {
        endeBereich = startBereich;
      }
      endeId = new BereichselementId(endeBereich, endeName);
    }

    return this.parcours.getFahrstrassen(startId, endeId, filter);
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
      if (!fahrstrasse.reservieren(FahrstrassenReservierungsTyp.ofCode(reservierungsTypAsString))) {
        return ResponseFactory.createConflictResponse();
      }
    } catch (IllegalArgumentException e) {
      return ResponseFactory.createBadRequestResponse();
    }

    return ResponseFactory.createNoContentResponse();
  }

  @Inject
  // Event<String> eventSource;
  EventFirer eventSource;

  @Path("event")
  @PUT
  @Consumes("*/*")
  public void fireEvent() {
    String event = "Event of " + new Date();
    this.eventSource.fire(event);
    // this.eventSource.fireAsync(event);
  }
}

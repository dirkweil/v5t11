package de.gedoplan.v5t11.fahrstrassen.webservice;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.fahrstrassen.persistence.FahrstrassenStatusRepository;
import de.gedoplan.v5t11.util.domain.attribute.BereichselementId;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenFilter;
import de.gedoplan.v5t11.util.domain.attribute.FahrstrassenReservierungsTyp;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;
import de.gedoplan.v5t11.util.webservice.ResponseFactory;

import java.util.List;
import java.util.stream.Collectors;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.jboss.logging.Logger;

@Path("fahrstrasse")
@ApplicationScoped
@Transactional(rollbackOn = Exception.class)
public class FahrstrasseResource {

  @Inject
  Parcours parcours;

  @Inject
  FahrstrassenStatusRepository fahrstrassenStatusRepository;

  @Inject
  Logger log;

  @GET
  @Path("{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public Response getFahrstrasse(@PathParam("id") BereichselementId id) {
    Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse(id);
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

    FahrstrassenFilter filter = filterAsString != null ? FahrstrassenFilter.fromString(filterAsString, false) : null;

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
  @Path("{id}/reservierung")
  @Consumes(MediaType.WILDCARD)
  public Response reserviereFahrstrasse(@PathParam("id") BereichselementId id, String reservierungsTypAsString) {
    if (this.log.isDebugEnabled()) {
      this.log.debug(String.format("reserviereFahrstrasse: id=%s, reservierungsTyp=%s", id, reservierungsTypAsString));
    }

    Fahrstrasse fahrstrasse = this.parcours.getFahrstrasse(id);
    if (fahrstrasse == null) {
      return ResponseFactory.createNotFoundResponse();
    }

    try {
      if (!fahrstrasse.reservieren(FahrstrassenReservierungsTyp.fromString(reservierungsTypAsString))) {
        return ResponseFactory.createConflictResponse();
      }
    } catch (IllegalArgumentException e) {
      return ResponseFactory.createBadRequestResponse();
    }

    return ResponseFactory.createNoContentResponse();
  }

}

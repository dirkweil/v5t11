package de.gedoplan.v5t11.util.webservice;

import jakarta.json.bind.Jsonb;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public abstract class ResponseFactory {

  public static Response createNoContentResponse() {
    return Response.noContent().build();
  }

  public static Response createNotFoundResponse() {
    return Response.status(Response.Status.NOT_FOUND).build();
  }

  public static Response createBadRequestResponse() {
    return Response.status(Response.Status.BAD_REQUEST).build();
  }

  public static Response createConflictResponse() {
    return Response.status(Response.Status.CONFLICT).build();
  }

  public static Response createJsonResponse(Object object, Jsonb jsonb) {
    return Response.ok()
      .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
      .entity(jsonb.toJson(object))
      .build();
  }

}

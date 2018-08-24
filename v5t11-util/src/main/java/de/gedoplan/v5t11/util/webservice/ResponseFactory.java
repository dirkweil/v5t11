package de.gedoplan.v5t11.util.webservice;

import javax.json.bind.Jsonb;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

public abstract class ResponseFactory {

  public static Response createNotFoundReponse() {
    return Response.status(Response.Status.NOT_FOUND).build();
  }

  public static Response createJsonResponse(Object object, Jsonb jsonb) {
    return Response.ok()
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON)
        .entity(jsonb.toJson(object))
        .build();
  }
}

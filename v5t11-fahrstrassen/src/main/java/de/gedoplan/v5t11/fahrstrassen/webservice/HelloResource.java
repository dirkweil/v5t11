package de.gedoplan.v5t11.fahrstrassen.webservice;

import javax.enterprise.context.ApplicationScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("hello")
@ApplicationScoped
public class HelloResource {

  @GET
  @Produces(MediaType.TEXT_PLAIN)
  public String getHello() {
    return "Hello!";
  }
}

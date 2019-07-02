package de.gedoplan.v5t11.status.webservice;

import de.gedoplan.v5t11.util.webservice.provider.JsonMessageBodyReader;
import de.gedoplan.v5t11.util.webservice.provider.JsonMessageBodyWriter;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@ApplicationPath("rs")
public class RestApplication extends Application {

  @Override
  public Set<Class<?>> getClasses() {
    return Stream.of(
        // Services
        GleisResource.class,
        LokResource.class,
        // LokcontrollerResource.class,
        SignalResource.class,
        WeicheResource.class,
        ZentraleResource.class,

        // Provider
        JsonMessageBodyReader.FULL.class,
        JsonMessageBodyWriter.FULL.class)
        .collect(Collectors.toSet());
  }
}

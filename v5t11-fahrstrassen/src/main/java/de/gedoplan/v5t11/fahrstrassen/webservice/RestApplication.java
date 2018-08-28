package de.gedoplan.v5t11.fahrstrassen.webservice;

import de.gedoplan.v5t11.util.webservice.provider.JsonMessageBodyReader;
import de.gedoplan.v5t11.util.webservice.provider.JsonMessageBodyWriter;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.Dependent;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

@Dependent
@ApplicationPath("rs")
public class RestApplication extends Application {

  @Override
  public Set<Class<?>> getClasses() {
    return Stream.of(
        // Services
        FahrstrasseResource.class,

        // Provider
        JsonMessageBodyReader.FULL.class,
        JsonMessageBodyWriter.FULL.class)
        .collect(Collectors.toSet());
  }
}

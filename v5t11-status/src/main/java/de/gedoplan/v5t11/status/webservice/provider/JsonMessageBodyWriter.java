package de.gedoplan.v5t11.status.webservice.provider;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Provider;

/**
 * Message Body Writer für Object -> application/json.
 *
 * In JEE8 ist ein MBR fuer JSON bereits enthalten, allerdings enthaelt bspw. die Implementierung in WildFly 13
 * (RestEasy, Jackson2) einen Bug, der u. a. JAXB-Annotationen faelschlicherweise beruecksichtigt. Der hier
 * vorliegende MBR nutzt ausschliesslich JSON-B.
 *
 * @author dw
 *
 */
@Provider
@Produces("application/json")
public class JsonMessageBodyWriter implements MessageBodyWriter<Object> {
  @Override
  public boolean isWriteable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return true;
  }

  @Override
  public long getSize(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return -1;
  }

  @Override
  public void writeTo(Object object, Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType,
      MultivaluedMap<String, Object> httpHeaders, OutputStream entityStream)
      throws IOException, WebApplicationException {

    Jsonb jsonb = JsonbBuilder.create();
    String json = jsonb.toJson(object);

    // TODO CharSet aus Header uebernehmen
    entityStream.write(json.getBytes());
  }
}

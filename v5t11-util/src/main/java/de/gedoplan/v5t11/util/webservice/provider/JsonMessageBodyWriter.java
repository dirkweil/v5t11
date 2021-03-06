package de.gedoplan.v5t11.util.webservice.provider;

import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.json.bind.Jsonb;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

/**
 * Message Body Writer für Object -> application/json.
 *
 * In JEE8 ist ein MBW fuer JSON bereits enthalten, allerdings enthaelt bspw. die Implementierung in WildFly 13
 * (RestEasy, Jackson2) einen Bug, der u. a. JAXB-Annotationen faelschlicherweise beruecksichtigt.
 * Der hier enthaltenen MBW nutzen ausschliesslich JSON-B.
 *
 * @author dw
 *
 */
public abstract class JsonMessageBodyWriter implements MessageBodyWriter<Object> {

  // @Provider
  @Produces("application/json")
  public static class SHORT extends JsonMessageBodyWriter {

    public SHORT() {
      super(JsonbWithIncludeVisibility.SHORT);
    }
  }

  // @Provider
  @Produces("application/json")
  public static class FULL extends JsonMessageBodyWriter {

    public FULL() {
      super(JsonbWithIncludeVisibility.FULL);
    }
  }

  private Jsonb jsonb;

  protected JsonMessageBodyWriter(Jsonb jsonb) {
    this.jsonb = jsonb;
  }

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

    String json = this.jsonb.toJson(object);

    // TODO CharSet aus Header uebernehmen
    entityStream.write(json.getBytes());
  }
}

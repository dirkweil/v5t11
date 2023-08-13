package de.gedoplan.v5t11.util.webservice.provider;

import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

import jakarta.json.bind.Jsonb;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyWriter;
import jakarta.ws.rs.ext.Provider;

/**
 * Message Body Writer für Object -> application/json.
 * <p>
 * In JEE8 ist ein MBW fuer JSON bereits enthalten, allerdings enthaelt bspw. die Implementierung in WildFly 13
 * (RestEasy, Jackson2) einen Bug, der u. a. JAXB-Annotationen faelschlicherweise beruecksichtigt.
 * Der hier enthaltenen MBW nutzen ausschliesslich JSON-B.
 *
 * @author dw
 */
public abstract class JsonMessageBodyWriter implements MessageBodyWriter<Object> {

  private static Charset CHARSET_UTF_8 = Charset.forName("UTF-8");

  // @Provider
  // @Produces("application/json")
  // public static class SHORT extends JsonMessageBodyWriter {
  //
  // public SHORT() {
  // super(JsonbWithIncludeVisibility.SHORT);
  // }
  // }

  @Provider
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

    entityStream.write(json.getBytes(CHARSET_UTF_8));
  }
}

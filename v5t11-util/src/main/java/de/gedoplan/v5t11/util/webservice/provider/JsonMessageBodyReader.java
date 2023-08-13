package de.gedoplan.v5t11.util.webservice.provider;

import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import jakarta.json.bind.Jsonb;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedMap;
import jakarta.ws.rs.ext.MessageBodyReader;
import jakarta.ws.rs.ext.Provider;

/**
 * Message Body Reader für application/json -> Object.
 * <p>
 * In JEE8 ist ein MBR fuer JSON bereits enthalten, allerdings enthaelt bspw. die Implementierung in WildFly 13
 * (RestEasy, Jackson2) einen Bug, der u. a. JAXB-Annotationen faelschlicherweise beruecksichtigt.
 * Die hier enthaltenen MBR nutzen ausschliesslich JSON-B.
 *
 * @author dw
 */
public abstract class JsonMessageBodyReader implements MessageBodyReader<Object> {

  @Provider
  @Consumes("application/json")
  public static class SHORT extends JsonMessageBodyReader {

    public SHORT() {
      super(JsonbWithIncludeVisibility.SHORT);
    }
  }

  @Provider
  @Consumes("application/json")
  public static class FULL extends JsonMessageBodyReader {

    public FULL() {
      super(JsonbWithIncludeVisibility.FULL);
    }
  }

  private Jsonb jsonb;

  protected JsonMessageBodyReader(Jsonb jsonb) {
    this.jsonb = jsonb;
  }

  @Override
  public boolean isReadable(Class<?> type, Type genericType, Annotation[] annotations, MediaType mediaType) {
    return true;
  }

  @Override
  public Object readFrom(Class<Object> type, Type genericType, Annotation[] annotations, MediaType mediaType, MultivaluedMap<String, String> httpHeaders, InputStream inputStream)
    throws IOException, WebApplicationException {

    return this.jsonb.fromJson(inputStream, genericType);
  }

}

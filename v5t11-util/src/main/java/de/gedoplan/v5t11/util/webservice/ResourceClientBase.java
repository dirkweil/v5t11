package de.gedoplan.v5t11.util.webservice;

import de.gedoplan.v5t11.util.webservice.provider.JsonMessageBodyReader;

import javax.annotation.PreDestroy;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Basisklasse zum Aufbau von REST-Client-Proxies
 *
 * @author dw
 */
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class ResourceClientBase {

  protected Client client;
  protected WebTarget baseTarget;

  protected ResourceClientBase(String baseUrl, String resourceUrl) {
    this.client = ClientBuilder.newBuilder().register(JsonMessageBodyReader.FULL.class).build();
    this.baseTarget = this.client.target(baseUrl).path(resourceUrl);
  }

  @PreDestroy
  void close() {
    if (this.client != null) {
      this.client.close();
    }
  }
}

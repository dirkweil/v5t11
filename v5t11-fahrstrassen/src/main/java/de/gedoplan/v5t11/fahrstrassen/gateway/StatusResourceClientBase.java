package de.gedoplan.v5t11.fahrstrassen.gateway;

import de.gedoplan.v5t11.util.webservice.provider.JsonMessageBodyReader;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

public abstract class StatusResourceClientBase {

  protected Client client;
  protected WebTarget StatusBaseTarget;

  @PostConstruct
  void createClient() {
    this.client = ClientBuilder.newBuilder().register(JsonMessageBodyReader.FULL.class).build();
    this.StatusBaseTarget = this.client.target("http://localhost:8080/rs");
  }

  @PreDestroy
  void close() {
    this.client.close();
  }
}

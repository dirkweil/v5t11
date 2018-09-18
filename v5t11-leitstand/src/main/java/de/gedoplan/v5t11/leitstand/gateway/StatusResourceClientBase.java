package de.gedoplan.v5t11.leitstand.gateway;

import de.gedoplan.v5t11.leitstand.service.ConfigService;
import de.gedoplan.v5t11.util.webservice.provider.JsonMessageBodyReader;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.inject.Inject;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;

//TODO Dies ist eine exakte Kopie aus v5t11-fahrstrassen; geht das eleganter?

public abstract class StatusResourceClientBase {

  @Inject
  ConfigService configService;

  protected Client client;
  protected WebTarget StatusBaseTarget;

  @PostConstruct
  void createClient() {
    this.client = ClientBuilder.newBuilder().register(JsonMessageBodyReader.FULL.class).build();
    this.StatusBaseTarget = this.client.target(this.configService.getStatusRestUrl());
  }

  @PreDestroy
  void close() {
    this.client.close();
  }
}

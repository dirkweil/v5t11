package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.v5t11.fahrzeuge.service.ConfigService;
import de.gedoplan.v5t11.util.jsf.NavigationItem;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@Dependent
public class NavigationProducer {

  @Inject
  ConfigService configService;

  private String urlPrefix;

  @PostConstruct
  void postConstruct() {
    this.urlPrefix = this.configService.getFahrzeugeWebUrl() + "/";
  }

  @Produces
  NavigationItem getWebsocketNavigationItem() {
    return new NavigationItem("Websocket", "MyFaces", this.urlPrefix + "socket.xhtml", "fa fa-arrows", 310);
  }

  @Produces
  NavigationItem getTableNavigationItem() {
    return new NavigationItem("Table", "MyFaces", this.urlPrefix + "table.xhtml", "fa fa-table", 310);
  }
}

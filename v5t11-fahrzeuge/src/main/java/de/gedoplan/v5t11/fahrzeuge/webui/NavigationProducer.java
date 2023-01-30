package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.v5t11.fahrzeuge.service.ConfigService;
import de.gedoplan.v5t11.util.jsf.NavigationItem;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import java.util.List;

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
  @ApplicationScoped
  List<NavigationItem> getFahrzeugManagementNavigationItem() {
    return List.of(new NavigationItem("Fahrzeug-Management", "Fahrzeuge", this.urlPrefix + "view/fahrzeugManagement.xhtml", "pi pi-th-large", 210));
  }
}

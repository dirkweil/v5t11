package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.v5t11.fahrzeuge.service.ConfigService;
import de.gedoplan.v5t11.util.jsf.NavigationItem;

import java.util.List;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

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
    return List.of(
      new NavigationItem("Fahrzeug-Management", "Fahrzeuge", this.urlPrefix + "view/fahrzeugManagement.xhtml", "pi pi-th-large", 210),
      new NavigationItem("Fahrzeug-Management (alt)", "Fahrzeuge", this.urlPrefix + "view/fahrzeugManagement_alt.xhtml", "pi pi-th-large", 211)
    );
  }
}

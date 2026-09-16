package de.gedoplan.v5t11.status.webui;

import de.gedoplan.v5t11.status.service.ConfigService;
import de.gedoplan.v5t11.util.jsf.NavigationItem;

import org.jboss.logging.Logger;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import java.util.List;

@Dependent
public class NavigationProducer {

  @Inject
  ConfigService configService;

  @Inject
  Logger logger;

  private String urlPrefix;

  @PostConstruct
  void postConstruct() {
    this.urlPrefix = this.configService.getStatusWebUrl() + "/";

    this.logger.debugf("urlPrefix: %s", this.urlPrefix);
  }

  @Produces
  @ApplicationScoped
  List<NavigationItem> getStatusNavigationItem() {
    return List.of(
      new NavigationItem("Baustein-Programmierung", "Steuerung", this.urlPrefix + "ui/baustein-programmierung", "pi pi-box", 825),
      new NavigationItem("System-Status", "Steuerung", this.urlPrefix + "ui/system-status", "pi pi-info-circle", 805),
      new NavigationItem("System-Control", "Steuerung", this.urlPrefix + "ui/system-control", "pi pi-play", 815)
    );
  }

}

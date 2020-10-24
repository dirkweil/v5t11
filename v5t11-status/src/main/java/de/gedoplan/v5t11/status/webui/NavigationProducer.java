package de.gedoplan.v5t11.status.webui;

import de.gedoplan.v5t11.status.service.ConfigService;
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
    this.urlPrefix = this.configService.getStatusWebUrl() + "/";
  }

  @Produces
  NavigationItem getSystemStatusNavigationItem() {
    return new NavigationItem("System-Status", "Steuerung", this.urlPrefix + "view/systemStatus.xhtml", "fa fa-info", 110);
  }

  @Produces
  NavigationItem getSystemControlNavigationItem() {
    return new NavigationItem("System-Control", "Steuerung", this.urlPrefix + "view/systemControl.xhtml", "fa fa-play", 120);
  }

  @Produces
  NavigationItem getBausteinProgrammierungNavigationItem() {
    return new NavigationItem("Baustein-Programmierung", "Steuerung", this.urlPrefix + "view/bausteinProgrammierung.xhtml", "fa fa-cube", 130);
  }
}

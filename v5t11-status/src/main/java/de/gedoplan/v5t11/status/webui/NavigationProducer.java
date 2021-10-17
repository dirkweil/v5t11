package de.gedoplan.v5t11.status.webui;

import de.gedoplan.v5t11.status.service.ConfigService;
import de.gedoplan.v5t11.util.jsf.NavigationItem;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

import org.jboss.logging.Logger;

@ApplicationScoped
public class NavigationProducer {

  @Inject
  ConfigService configService;

  @Inject
  Logger logger;

  private String urlPrefix;

  @PostConstruct
  void postConstruct() {
    this.urlPrefix = this.configService.getStatusWebUrl() + "/";

    logger.debugf("urlPrefix: %s", urlPrefix);
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

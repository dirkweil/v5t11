package de.gedoplan.v5t11.leitstand.webui;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.service.ConfigService;
import de.gedoplan.v5t11.util.jsf.NavigationItem;

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
  Leitstand leitstand;

  private String urlPrefix;

  @PostConstruct
  void postConstruct() {
    this.urlPrefix = this.configService.getLeitstandWebUrl() + "/";
  }

  @Produces
  @ApplicationScoped
  List<NavigationItem> getHbfNavigationItem() {
    return this.leitstand
      .getBereiche()
      .stream()
      .map(b -> new NavigationItem(b, "Stellwerk", this.urlPrefix + "view/stellwerk.xhtml?bereich=" + b, "pi pi-map", 0))
      .toList();
  }
}

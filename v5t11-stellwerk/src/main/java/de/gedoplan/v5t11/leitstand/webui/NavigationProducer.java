package de.gedoplan.v5t11.leitstand.webui;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.leitstand.service.ConfigService;
import de.gedoplan.v5t11.util.jsf.NavigationItem;

import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;

import java.util.ArrayList;
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
    List<NavigationItem> items = new ArrayList<>();
    for (String bereich : this.leitstand.getBereiche()) {
      items.add(new NavigationItem(bereich, "Stellwerk", this.urlPrefix + "ui/stellwerk/" + bereich, "pi pi-map", 0));
    }
    return items;
  }
}

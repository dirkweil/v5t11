package de.gedoplan.v5t11.util.jsf;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.logging.Log;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Named
@ApplicationScoped
public class NavigationPresenter {

  @Getter
  private ConcurrentMap<NavigationItem, NavigationItemState> navigationItems = new ConcurrentSkipListMap<>();

  @Getter
  @Setter
  @ToString
  public static class NavigationItemState {
    boolean mine;
    long lastHeartBeatMillis;
    boolean disabled;
  }

  @Inject
  Instance<NavigationItem> myNavigationItems;

  @Inject
  Log log;

  @PostConstruct
  void postConstruct() {
    this.myNavigationItems.forEach(ni -> registerNavigationItem(ni, true));
    registerNavigationItem(new NavigationItem("home", "Allgemein", "/index.xhtml", "fa fa-home", 910), true);
    // registerNavigationItem(new NavigationItem("refresh", "Allgemein", null, "fa fa-refresh", 920), true);
  }

  public void registerNavigationItem(NavigationItem navigationItem, boolean mine) {
    NavigationItemState navigationItemState = new NavigationItemState();
    navigationItemState.mine = true;
    this.navigationItems.put(navigationItem, navigationItemState);

    this.log.debug(navigationItem + ": " + navigationItemState);
  }

  public DefaultMenuModel getMenuModel() {
    DefaultMenuModel menuModel = new DefaultMenuModel();
    Map<String, DefaultSubMenu> submenus = new HashMap<>();
    this.navigationItems.forEach((navigationItem, navigationItemState) -> {
      // Submenu zu Kategorie holen; bei Bedarf erzeugen
      DefaultSubMenu submenu = submenus.computeIfAbsent(navigationItem.getCategory(), category -> {
        DefaultSubMenu sm = DefaultSubMenu.builder().label(category).build();
        menuModel.getElements().add(sm);
        return sm;
      });

      // MenuItem hinzufügen
      submenu.getElements().add(navigationItem.toMenuItem(navigationItemState.disabled));
    });
    return menuModel;
  }
}

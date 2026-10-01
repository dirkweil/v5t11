package de.gedoplan.v5t11.vaadincommon.ui;

import de.gedoplan.v5t11.vaadincommon.navigation.NavigationPresenter;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.icon.FontIcon;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;

import java.util.Map;
import java.util.TreeMap;

/**
 * Rendert das global föderierte Navigationsmenü als Vaadin {@link SideNav}.
 * <p>
 * Nutzt die {@link NavigationPresenter}-Instanz (und damit die über CDI-Events/Messaging föderierte
 * Menüpunkt-Liste), live aktualisiert über Vaadin-Push statt rohem Websocket-JS.
 */
public class VaadinNavigationMenu extends SideNav {

  private final NavigationPresenter navigationPresenter;

  private final Runnable menuChangeListener = this::scheduleRebuild;

  public VaadinNavigationMenu(NavigationPresenter navigationPresenter) {
    this.navigationPresenter = navigationPresenter;
    rebuild();
  }

  @Override
  protected void onAttach(AttachEvent attachEvent) {
    super.onAttach(attachEvent);
    this.navigationPresenter.addMenuChangeListener(this.menuChangeListener);
    rebuild();
  }

  @Override
  protected void onDetach(DetachEvent detachEvent) {
    this.navigationPresenter.removeMenuChangeListener(this.menuChangeListener);
    super.onDetach(detachEvent);
  }

  private void scheduleRebuild() {
    getUI().ifPresentOrElse(ui -> ui.access(this::rebuild), this::rebuild);
  }

  private void rebuild() {
    removeAll();

    Map<String, SideNavItem> categories = new TreeMap<>();
    this.navigationPresenter.getNavigationItems().forEach((navigationItem, state) -> {
      SideNavItem category = categories.computeIfAbsent(navigationItem.getCategory(), label -> {
        SideNavItem categoryItem = new SideNavItem(label);
        categoryItem.setExpanded(true);
        categoryItem.addClassName("nav-category");
        addItem(categoryItem);
        return categoryItem;
      });

      SideNavItem item;
      if (state.isDisabled()) {
        // setEnabled(false) funktioniert hier nicht: vaadin-side-nav-item erzwingt bei jeder
        // Änderung der Kinderzahl des Eltern-Items (_itemsCount) den disabled-Zustand aller Kinder
        // auf den des (nie disabled) Eltern-Items zurück (vaadin-side-nav-item.js#updated:
        // "Ensure all the child items are disabled"). Stattdessen bewusst kein Pfad setzen: ohne
        // path ist der Eintrag weder navigierbar noch fokussierbar; die graue Darstellung kommt
        // über die CSS-Klasse "nav-item-disabled".
        item = new SideNavItem(navigationItem.getName());
        item.addClassName("nav-item-disabled");
      } else {
        item = new SideNavItem(navigationItem.getName(), navigationItem.getUrl());
      }
      item.setPrefixComponent(new FontIcon(navigationItem.getIcon().split(" ")));
      category.addItem(item);
    });
  }
}

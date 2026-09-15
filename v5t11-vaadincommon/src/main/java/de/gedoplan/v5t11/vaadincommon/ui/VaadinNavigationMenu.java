package de.gedoplan.v5t11.vaadincommon.ui;

import de.gedoplan.v5t11.util.jsf.NavigationPresenter;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;

import java.util.Map;
import java.util.TreeMap;

/**
 * Vaadin-Pendant zu {@code NavigationPresenter#getMenuModel()} aus v5t11-jsfcommon.
 * <p>
 * Nutzt bewusst dieselbe {@link NavigationPresenter}-Instanz (und damit dieselbe, bereits über CDI-Events/Messaging
 * föderierte Menüpunkt-Liste) wie das bestehende JSF-Menü, statt die Föderationslogik zu duplizieren. Nur Rendering
 * (Vaadin {@link SideNav} statt PrimeFaces {@code p:menu}) und Live-Refresh-Transport (Vaadin-Push statt rohem
 * Websocket-JS) sind neu.
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
      category.addItem(item);
    });
  }
}

package de.gedoplan.v5t11.vaadincommon.ui;

import de.gedoplan.v5t11.util.jsf.NavigationPresenter;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.H1;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

/**
 * Gemeinsames Layout für alle Vaadin-Views der v5t11-Services, Pendant zum bisherigen Facelets-Template
 * {@code WEB-INF/templates/v5t11.xhtml} aus v5t11-jsfcommon.
 */
public class MainLayout extends AppLayout {

  @Inject
  NavigationPresenter navigationPresenter;

  @PostConstruct
  void init() {
    H1 title = new H1("v5t11");
    title.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");

    addToNavbar(new DrawerToggle(), title);
    addToDrawer(new VaadinNavigationMenu(this.navigationPresenter));
  }
}

package de.gedoplan.v5t11.vaadincommon.ui;

import de.gedoplan.v5t11.vaadincommon.navigation.NavigationPresenter;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Inject;

/**
 * Gemeinsames Layout für alle Vaadin-Views der v5t11-Services.
 */
public class MainLayout extends AppLayout {

  @Inject
  NavigationPresenter navigationPresenter;

  private Div extraArea;

  @PostConstruct
  void init() {
    H1 title = new H1("v5t11");
    title.getStyle().set("font-size", "var(--lumo-font-size-l)").set("margin", "0");

    addToNavbar(new DrawerToggle(), title);

    // Lumos Standardbreite (16em) reicht für Navigation + setExtraContent(...)-Inhalte nicht immer aus - z. B.
    // passt StellwerkViews Control-Panel mit vier nebeneinander stehenden Signal-Stellung-Toggle-Buttons
    // ("H"/"F"/"L"/"R") darin sonst nicht in eine Zeile und bricht um.
    getStyle().set("--vaadin-app-layout-drawer-width", "22em");

    this.extraArea = new Div();
    addToDrawer(new VaadinNavigationMenu(this.navigationPresenter), this.extraArea);
  }

  /**
   * Pendant zum JSF-Template-Slot {@code <ui:insert name="extra"/>} (unterhalb des Navigationsmenüs im
   * Drawer, für view-spezifische Steuerelemente wie Filter/Aktionen). Von der jeweiligen View in
   * {@code onAttach()} zu setzen und in {@code onDetach()} wieder mit leerem Aufruf zu räumen.
   */
  public void setExtraContent(Component... components) {
    this.extraArea.removeAll();
    this.extraArea.add(components);
  }
}

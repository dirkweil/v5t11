package de.gedoplan.v5t11.status.webservice;

import de.gedoplan.v5t11.vaadincommon.navigation.NavigationItem;
import de.gedoplan.v5t11.vaadincommon.navigation.NavigationPresenter;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("menu")
@Dependent
public class MenuResource {

  @Inject
  NavigationPresenter navigationPresenter;

  @GET
  @Path("newSubmenu")
  public void newSubmenu() {
    this.navigationPresenter.registerNavigationItem(new NavigationItem("Otto", "GEDOPLAN", "https://gedoplan.de", null, 115), true, false);
  }

  @GET
  @Path("disable")
  public void disable() {
    this.navigationPresenter.getNavigationItems().forEach((item, state) -> {
      if (item.getCategory().equals("Steuerung")) {
        state.setDisabled(true);
      }
    });
  }

  @GET
  @Path("enable")
  public void enable() {
    this.navigationPresenter.getNavigationItems().forEach((item, state) -> {
      if (item.getCategory().equals("Steuerung")) {
        state.setDisabled(false);
      }
    });
  }
}

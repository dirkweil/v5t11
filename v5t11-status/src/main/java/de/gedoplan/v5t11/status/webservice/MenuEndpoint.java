package de.gedoplan.v5t11.status.webservice;

import de.gedoplan.v5t11.util.jsf.NavigationItem;
import de.gedoplan.v5t11.util.jsf.NavigationPresenter;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("menu")
@Dependent
public class MenuEndpoint {

  @Inject
  NavigationPresenter navigationPresenter;

  @GET
  @Path("newItem")
  public void newItem() {
    this.navigationPresenter.registerNavigationItem(new NavigationItem("Hugo", "Steuerung", "https://gedoplan.de", null, 115), true);
  }

  @GET
  @Path("newSubmenu")
  public void newSubmenu() {
    this.navigationPresenter.registerNavigationItem(new NavigationItem("Otto", "GEDOPLAN", "https://gedoplan.de", null, 115), true);
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

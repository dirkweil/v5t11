package de.gedoplan.v5t11.util.jsf;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import lombok.Getter;

@Named
@ApplicationScoped
public class NavigationPresenter {

  @Getter
  private MenuModel menuModel;

  @PostConstruct
  void postConstruct() {
    this.menuModel = new DefaultMenuModel();

    DefaultSubMenu statusSubmenu = DefaultSubMenu.builder()
        .label("Status")
        .build();

    this.menuModel.getElements().add(statusSubmenu);

    statusSubmenu.getElements().add(
        DefaultMenuItem.builder()
            .value("System-Status")
            .url("http://localhost:8080/view/systemStatus.xhtml")
            .icon("fa fa-info")
            .build());

    statusSubmenu.getElements().add(
        DefaultMenuItem.builder()
            .value("System-Control")
            .url("http://localhost:8080/view/systemControl.xhtml")
            .icon("fa fa-play")
            .build());

    statusSubmenu.getElements().add(
        DefaultMenuItem.builder()
            .value("Baustein-Programmierung")
            .url("http://localhost:8080/view/bausteinProgrammierung.xhtml")
            .icon("fa fa-cube")
            .build());

    DefaultSubMenu miscSubmenu = DefaultSubMenu.builder()
        .label("Allgemein")
        .build();

    this.menuModel.getElements().add(miscSubmenu);

    miscSubmenu.getElements().add(
        DefaultMenuItem.builder()
            .value("home")
            .icon("fa fa-home")
            .url("/index.xhtml")
            .ajax(false)
            .build());

    miscSubmenu.getElements().add(
        DefaultMenuItem.builder()
            .value("refresh")
            .icon("fa fa-refresh")
            .ajax(false)
            .build());
  }
}

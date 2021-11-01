package de.gedoplan.v5t11.util.jsf;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Instance;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.interceptor.Interceptor;

import org.jboss.logging.Logger;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;

import de.gedoplan.v5t11.util.cdi.EventFirer;
import io.quarkus.runtime.StartupEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * Presenter für das globale Navigationsmenü.
 * 
 * Die Anwendung muss ihre Menü-Einträge mit Hilfe von Producern für den Typ {@Link NavigationItem} bereitstellen. Sie werden
 * initial genutzt, um das Menü mit Eigeneinträgen zu bestücken.
 * 
 * Die Eigeneinträge werden regelmäßig als Heartbeat an die anderen Anwendungen versendet. Dazu muss die Anwendung einen
 * CDI-Observer besitzen, der Events des Typs {@Link NavigationItem} per Messaging versendet. Alle V5t11-Services verhalten
 * sich so.
 * 
 * Somit gehen die versendeten Eigeneinträge einer Anwendung als Messages bei den anderen Anwendungen ein. Die Anwendungen
 * müssen diese eingehenden {@Link NavigationItem NavigationItems} an {@link #heartBeat(NavigationItem)} propagieren. Sie
 * werden dann als Fremdeinträge im Menü eingebaut.
 * 
 * Während der Heartbeat-Verarbeitung wird überprüft, ob alle Fremdeinträge innerhalb der letzten Zeit aufgefrischt wurden,
 * ob die aussendende Anwendung also noch "lebt". Falls nicht, werden die Einträge deaktiviert.
 * 
 * @author dw
 *
 */
@Named
@ApplicationScoped
public class NavigationPresenter {

  private static final long HEARTBEAT_MILLIS = 10000L;

  @Getter
  private ConcurrentMap<NavigationItem, NavigationItemState> navigationItems = new ConcurrentSkipListMap<>();

  @Getter
  @Setter
  @ToString
  public static class NavigationItemState {
    boolean mine;
    public boolean broadcast;
    long lastHeartBeatMillis;
    boolean disabled;
  }

  @Inject
  Instance<NavigationItem> myNavigationItems;

  private AtomicBoolean menuChanged = new AtomicBoolean();

  // NavigationItem
  @Inject
  EventFirer eventFirer;

  @Inject
  Logger log;

  @Inject
  @Push
  PushContext menuRefresh;

  private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  void startup(@Observes StartupEvent startupEvent) {
  }

  @PostConstruct
  void postConstruct() {
    this.myNavigationItems.forEach(ni -> registerNavigationItem(ni, true, true));
    registerNavigationItem(new NavigationItem("home", "Allgemein", "/index.xhtml", "fa fa-home", 910), true, false);

    this.scheduler.scheduleAtFixedRate(this::heartBeat, HEARTBEAT_MILLIS / 3, HEARTBEAT_MILLIS, TimeUnit.MILLISECONDS);

    this.log.debug("Started scheduler");
  }

  @PreDestroy
  void preDestroy() {
    this.log.debug("Stopping scheduler");

    this.scheduler.shutdown();
  }

  public void registerNavigationItem(NavigationItem navigationItem, boolean mine, boolean broadcast) {
    NavigationItemState navigationItemState = new NavigationItemState();
    navigationItemState.mine = mine;
    navigationItemState.broadcast = broadcast;
    navigationItemState.lastHeartBeatMillis = System.currentTimeMillis();

    StringBuilder action = new StringBuilder();

    this.navigationItems.merge(navigationItem, navigationItemState, (existing, incoming) -> {
      if (existing.mine) {
        action.append("Fixierter Eigeneintrag");
        return existing;
      }

      incoming.mine = false;
      incoming.broadcast = false;
      incoming.disabled = false;

      action.append("Refresh Fremdeintrag");

      if (existing.disabled) {
        this.menuChanged.set(true);
      }

      return incoming;
    });

    if (action.length() == 0) {
      action.append("Neuer ");
      action.append(mine ? "Eigeneintrag" : "Fremdeintrag");

      this.menuChanged.set(true);
    }

    if (this.log.isDebugEnabled()) {
      action.append(": ");
      action.append(navigationItem);
      this.log.debug(action);
    }
  }

  public void heartBeat() {
    long minRefreshMillis = System.currentTimeMillis() - HEARTBEAT_MILLIS * 2L;
    this.navigationItems.forEach((navigationItem, navigationItemState) -> {
      if (navigationItemState.mine) {
        if (navigationItemState.broadcast) {
          try {
            this.eventFirer.fire(navigationItem);
          } catch (Exception e) {
            this.log.warn("Kann NavigationItem nicht senden: " + e);
          }
        }
      } else {
        if (!navigationItemState.disabled && navigationItemState.lastHeartBeatMillis < minRefreshMillis) {
          if (this.log.isDebugEnabled()) {
            this.log.debug("Disable " + navigationItem);
          }
          navigationItemState.disabled = true;
          this.menuChanged.set(true);
        }
      }
    });

    if (this.menuChanged.compareAndSet(true, false)) {
      int sessionCount = this.menuRefresh.send("menuRefresh").size();
      if (this.log.isDebugEnabled()) {
        this.log.debug("Menu refreshen (" + sessionCount + " views)");
      }
    }
  }

  public void heartBeat(NavigationItem navigationItem) {
    registerNavigationItem(navigationItem, false, false);
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

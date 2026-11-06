package de.gedoplan.v5t11.vaadincommon.navigation;

import de.gedoplan.v5t11.util.cdi.EventFirer;

import io.quarkus.runtime.StartupEvent;
import org.jboss.logging.Logger;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.event.Observes;
import jakarta.enterprise.inject.Instance;
import jakarta.inject.Inject;

import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Presenter für das globale Navigationsmenü.
 * <p>
 * Die Anwendung muss ihre Menü-Einträge mit Hilfe von Producern für den Typ {@Link NavigationItem} bereitstellen. Sie werden
 * initial genutzt, um das Menü mit Eigeneinträgen zu bestücken.
 * <p>
 * Die Eigeneinträge werden regelmäßig als Heartbeat an die anderen Anwendungen versendet. Dazu muss die Anwendung einen
 * CDI-Observer besitzen, der Events des Typs {@Link NavigationItem} per Messaging versendet. Alle V5t11-Services verhalten
 * sich so.
 * <p>
 * Somit gehen die versendeten Eigeneinträge einer Anwendung als Messages bei den anderen Anwendungen ein. Die Anwendungen
 * müssen diese eingehenden {@Link NavigationItem NavigationItems} an {@link #heartBeat(NavigationItem)} propagieren. Sie
 * werden dann als Fremdeinträge im Menü eingebaut.
 * <p>
 * Während der Heartbeat-Verarbeitung wird überprüft, ob alle Fremdeinträge innerhalb der letzten Zeit aufgefrischt wurden,
 * ob die aussendende Anwendung also noch "lebt". Falls nicht, werden die Einträge deaktiviert.
 *
 * @author dw
 */
@ApplicationScoped
public class NavigationPresenter {

  private static final long HEARTBEAT_MILLIS = 10000L;

  @Inject
  Logger logger;

  /*
    * NavigationItems, die im Menü angezeigt werden sollen. Die Map ist nach den NavigationItems sortiert, damit die
    * Reihenfolge im Menü immer gleich ist.
    * Achtung: Die URL der NavigationItems kann sich ändern; die aktuelle URL wird im NavigationItemState gespeichert. 
   */
  @Getter
  private ConcurrentMap<NavigationItem, NavigationItemState> navigationItems = new ConcurrentSkipListMap<>();

  @Getter
  @Setter
  @ToString
  public static class NavigationItemState {
    boolean mine;
    public boolean broadcast;
    long lastHeartBeatMillis;
    String url;
    boolean disabled;
  }

  @Inject
  Instance<List<NavigationItem>> myNavigationItems;

  private AtomicBoolean menuChanged = new AtomicBoolean();

  /**
   * Wird von Vaadin-Frontends genutzt, um sich über Menüänderungen benachrichtigen zu lassen.
   */
  private final List<Runnable> menuChangeListeners = new CopyOnWriteArrayList<>();

  public void addMenuChangeListener(Runnable listener) {
    this.menuChangeListeners.add(listener);
  }

  public void removeMenuChangeListener(Runnable listener) {
    this.menuChangeListeners.remove(listener);
  }

  // NavigationItem
  @Inject
  EventFirer eventFirer;

  private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

  void startup(@Observes StartupEvent startupEvent) {
  }

  @PostConstruct
  void postConstruct() {
    this.myNavigationItems
      .stream()
      .flatMap(List::stream)
      .forEach(ni -> registerNavigationItem(ni, true, true));

    this.scheduler.scheduleAtFixedRate(this::heartBeat, HEARTBEAT_MILLIS / 3, HEARTBEAT_MILLIS, TimeUnit.MILLISECONDS);

    this.logger.debug("Started scheduler");
  }

  @PreDestroy
  void preDestroy() {
    this.logger.debug("Stopping scheduler");

    this.scheduler.shutdown();
  }

  /**
   * Registriert ein NavigationItem. Falls es bereits existiert, wird es aktualisiert.
   * 
   * @param navigationItem NavigationItem
   * @param mine Eigeneintrag?
   * @param broadcast als Heartbeat senden?
   */
  public void registerNavigationItem(NavigationItem navigationItem, boolean mine, boolean broadcast) {
    NavigationItemState navigationItemState = new NavigationItemState();
    navigationItemState.mine = mine;
    navigationItemState.broadcast = broadcast;
    navigationItemState.lastHeartBeatMillis = System.currentTimeMillis();
    navigationItemState.url = navigationItem.getUrl();

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

      if (existing.disabled || !existing.url.equals(incoming.url)) {
        this.menuChanged.set(true);
      }

      return incoming;
    });

    if (action.length() == 0) {
      action.append("Neuer ");
      action.append(mine ? "Eigeneintrag" : "Fremdeintrag");

      this.menuChanged.set(true);
    }

    if (this.logger.isDebugEnabled()) {
      action.append(": ");
      action.append(navigationItem);
      this.logger.debug(action);
    }

    // Menüänderungen an die Clients melden
    if (this.menuChanged.compareAndSet(true, false)) {
      this.menuChangeListeners.forEach(Runnable::run);
    }
  }

  /**
   * Wird regelmäßig aufgerufen, um die Heartbeats der Fremdeinträge zu prüfen und ggf. das Menü zu aktualisieren.
   * <p>
   * Außerdem werden die Eigeneinträge als Heartbeat versendet.
   */
  public void heartBeat() {
    long minRefreshMillis = System.currentTimeMillis() - HEARTBEAT_MILLIS * 2L;
    this.navigationItems.forEach((navigationItem, navigationItemState) -> {
      // Eigeneinträge als Heartbeat versenden
      if (navigationItemState.mine) {
        if (navigationItemState.broadcast) {
          try {
            this.eventFirer.fire(navigationItem);
          } catch (Exception e) {
            this.logger.warn("Kann NavigationItem nicht senden: " + e);
          }
        }
      } else {
        // Fremdeinträge prüfen, ob sie noch aktuell sind
        if (!navigationItemState.disabled && navigationItemState.lastHeartBeatMillis < minRefreshMillis) {
          if (this.logger.isDebugEnabled()) {
            this.logger.debug("Disable " + navigationItem);
          }
          navigationItemState.disabled = true;
          this.menuChanged.set(true);
        }
      }
    });

    // Menüänderungen an die Clients melden
    if (this.menuChanged.compareAndSet(true, false)) {
      this.menuChangeListeners.forEach(Runnable::run);
    }
  }

  public void heartBeat(NavigationItem navigationItem) {
    registerNavigationItem(navigationItem, false, false);
  }
}

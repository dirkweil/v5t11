package de.gedoplan.v5t11.status.messaging;

import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.cdi.Received;
import de.gedoplan.v5t11.util.jsf.NavigationItem;
import de.gedoplan.v5t11.util.jsf.NavigationPresenter;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

/**
 * Handler für eingehende Meldungen.
 * <p>
 * Die eingehenden Meldungen enthalten jeweils ein Objekt des passenden Typs als JSON in byte[] verpackt. Dieses Objekt
 * wird deserialisiert und mit dem Qualifier {@link Received @Received} als CDI Event gefeuert.
 * Ausnahme: {@link NavigationItem NavigationItems} werden direkt in {@link NavigationPresenter#heartBeat(NavigationItem)} gesteckt.
 * <p>
 * Achtung: Observer, die blockierend arbeiten (z. B. JPA-Code), müssen den Event asynchron verarbeiten, da in einem
 * Incoming Thread von Reactive Messaging keine blockierenden Operationen erlaubt sind!
 *
 * @author dw
 */
@ApplicationScoped
public class IncomingHandler {

  // @Received JoinInfo, Fahrzeug
  @Inject
  EventFirer eventFirer;

  @Inject
  Logger logger;

  @Inject
  NavigationPresenter navigationPresenter;

  @Incoming("fahrzeug")
  void fahrzeugDefined(String json) {
    fireReceived(json, Fahrzeug.class);
  }

  private void fireReceived(String json, Class<?> eventClass) {
    this.logger.debugf("Received: %s", json);
    Object receivedObject = JsonbWithIncludeVisibility.SHORT.fromJson(json, eventClass);
    this.eventFirer.fire(receivedObject, Received.Literal.INSTANCE);
  }

  @Incoming("navigation-in")
  void navigationChanged(String json) {
    this.logger.tracef("Received: %s", json);
    NavigationItem receivedObject = JsonbWithIncludeVisibility.SHORT.fromJson(json, NavigationItem.class);
    this.navigationPresenter.heartBeat(receivedObject);
  }

}

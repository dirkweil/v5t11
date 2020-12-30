package de.gedoplan.v5t11.status.messaging;

import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.cdi.Received;
import de.gedoplan.v5t11.util.domain.JoinInfo;
import de.gedoplan.v5t11.util.jsf.NavigationItem;
import de.gedoplan.v5t11.util.jsf.NavigationPresenter;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

/**
 * Handler für eingehende Meldungen.
 *
 * Die eingehenden Meldungen enthalten jeweils ein Objekt des passenden Typs als JSON in byte[] verpackt. Dieses Objekt
 * wird deserialisiert und mit dem Qualifier {@link Received @Received} als CDI Event gefeuert.
 * Ausnahme: {@link NavigationItem NavigationItems} werden direkt in {@link NavigationPresenter#heartBeat(NavigationItem)} gesteckt.
 * 
 * Achtung: Observer, die blockierend arbeiten (z. B. JPA-Code), müssen den Event asynchron verarbeiten, da in einem
 * Incoming Thread von Reactive Messaging keine blockierenden Operationen erlaubt sind!
 * 
 * @author dw
 *
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

  @Incoming("fahrzeug-def-in")
  void fahrzeugDefined(byte[] msg) {
    fireReceived(msg, Fahrzeug.class);
  }

  @Incoming("join-in")
  void appJoined(byte[] msg) {
    fireReceived(msg, JoinInfo.class);
  }

  private void fireReceived(byte[] msg, Class<?> eventClass) {
    String json = new String(msg);
    this.logger.debugf("Received: %s", json);
    Object receivedObject = JsonbWithIncludeVisibility.SHORT.fromJson(json, eventClass);
    this.eventFirer.fire(receivedObject, Received.Literal.INSTANCE);
  }

  @Incoming("navigation-in")
  void navigationChanged(byte[] msg) {
    String json = new String(msg);
    this.logger.tracef("Received: %s", json);
    NavigationItem receivedObject = JsonbWithIncludeVisibility.SHORT.fromJson(json, NavigationItem.class);
    this.navigationPresenter.heartBeat(receivedObject);
  }

}

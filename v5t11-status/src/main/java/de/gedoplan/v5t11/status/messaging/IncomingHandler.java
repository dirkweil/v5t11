package de.gedoplan.v5t11.status.messaging;

import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.cdi.Received;
import de.gedoplan.v5t11.util.domain.JoinInfo;
import de.gedoplan.v5t11.util.jsf.NavigationItem;
import de.gedoplan.v5t11.util.jsf.NavigationPresenter;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;

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

  @Inject
  EventFirer eventFirer;

  @Inject
  NavigationPresenter navigationPresenter;

  @Incoming("join-in")
  void appJoined(byte[] msg) {
    String json = new String(msg);
    JoinInfo receivedObject = JsonbWithIncludeVisibility.SHORT.fromJson(json, JoinInfo.class);
    this.eventFirer.fire(receivedObject, Received.Literal.INSTANCE);
  }

  @Incoming("navigation-in")
  void navigationChanged(byte[] msg) {
    String json = new String(msg);
    NavigationItem receivedObject = JsonbWithIncludeVisibility.SHORT.fromJson(json, NavigationItem.class);
    this.navigationPresenter.heartBeat(receivedObject);
  }

}

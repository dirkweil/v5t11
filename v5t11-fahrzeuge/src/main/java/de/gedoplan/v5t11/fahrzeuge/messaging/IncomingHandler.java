package de.gedoplan.v5t11.fahrzeuge.messaging;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
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
 * @author dw
 *
 */
@ApplicationScoped
public class IncomingHandler {

  @Inject
  Logger logger;

  @Inject
  EventFirer eventFirer;

  @Inject
  NavigationPresenter navigationPresenter;

  @Incoming("fahrzeug-in")
  void fahrzeugChanged(byte[] msg) {
    String json = new String(msg);
    Fahrzeug obj = JsonbWithIncludeVisibility.SHORT.fromJson(json, Fahrzeug.class);
    this.logger.debugf("Received %s: %s", obj, json);
    this.eventFirer.fire(obj, Received.Literal.INSTANCE);
  }

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

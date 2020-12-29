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

  @Incoming("fahrzeug-status-in")
  void fahrzeugChanged(byte[] msg) {
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

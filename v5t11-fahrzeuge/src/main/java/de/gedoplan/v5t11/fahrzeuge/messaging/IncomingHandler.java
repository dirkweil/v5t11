package de.gedoplan.v5t11.fahrzeuge.messaging;

import de.gedoplan.v5t11.util.jsf.NavigationItem;
import de.gedoplan.v5t11.util.jsf.NavigationPresenter;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;

/**
 * Handler für eingehende Meldungen.
 *
 * @author dw
 *
 */
@ApplicationScoped
public class IncomingHandler {

  // @Inject
  // EventFirer eventFirer;

  @Inject
  NavigationPresenter navigationPresenter;

  @Incoming("navigation-in")
  void navigationChanged(byte[] msg) {
    String json = new String(msg);
    NavigationItem receivedObject = JsonbWithIncludeVisibility.SHORT.fromJson(json, NavigationItem.class);
    this.navigationPresenter.heartBeat(receivedObject);
  }

}

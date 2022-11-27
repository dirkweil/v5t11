package de.gedoplan.v5t11.fahrzeuge.messaging;

import de.gedoplan.v5t11.fahrzeuge.entity.baustein.Zentrale;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrzeuge.entity.fahrweg.Weiche;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

  private static final Pattern STATUS_MSG_PATTERN = Pattern.compile("\\{\"(?<type>[^\"]+)\\s*\":(?<object>.*)\\}");

  @Incoming("status")
  void statusChanged(String json) {
    Matcher matcher = STATUS_MSG_PATTERN.matcher(json);
    if (matcher.matches()) {
      String typeAsString = matcher.group("type");
      Class<?> type = switch (typeAsString) {
        case "fahrzeug" -> Fahrzeug.class;
        default -> null;
      };

      if (type != null) {
        Object object = JsonbWithIncludeVisibility.SHORT.fromJson(matcher.group("object"), type);
        this.logger.debugf("Received %s: %s", object, json);
        this.eventFirer.fire(object, Received.Literal.INSTANCE);
      } else {
        this.logger.debugf("Received unknown status message of type %s", typeAsString);
      }
    } else {
      this.logger.warnf("Received illegal status message: %s", json);
    }
  }

  @Incoming("navigation-in")
  void navigationChanged(String json) {
    this.logger.tracef("Received: %s", json);
    NavigationItem receivedObject = JsonbWithIncludeVisibility.SHORT.fromJson(json, NavigationItem.class);
    this.navigationPresenter.heartBeat(receivedObject);
  }


}

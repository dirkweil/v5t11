package de.gedoplan.v5t11.fahrstrassen.messaging;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.cdi.Received;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handler für eingehende Meldungen.
 * <p>
 * Die eingehenden Meldungen enthalten jeweils ein Objekt des passenden Typs als JSON in byte[] verpackt. Dieses Objekt
 * wird deserialisiert und mit dem Qualifier {@link Received @Received} als CDI Event gefeuert.
 * <p>
 * Achtung: Observer, die blockierend arbeiten (z. B. JPA-Code), müssen den Event asynchron verarbeiten, da in einem
 * Incoming Thread von Reactive Messaging keine blockierenden Operationen erlaubt sind!
 *
 * @author dw
 */
@ApplicationScoped
public class IncomingHandler {

  @Inject
  Logger logger;

  @Inject
  EventFirer eventFirer;

  private static final Pattern STATUS_MSG_PATTERN = Pattern.compile("\\{\"(?<type>[^\"]+)\\s*\":(?<object>.*)\\}");

  @Incoming("status")
  void statusChanged(String json) {
    Matcher matcher = STATUS_MSG_PATTERN.matcher(json);
    if (matcher.matches()) {
      String typeAsString = matcher.group("type");
      Class<?> type = switch (typeAsString) {
        case "gleis" -> Gleis.class;
        case "signal" -> Signal.class;
        case "weiche" -> Weiche.class;
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

}

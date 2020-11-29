package de.gedoplan.v5t11.fahrstrassen.messaging;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.util.cdi.EventFirer;
import de.gedoplan.v5t11.util.cdi.Received;
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
  Logger logger;

  @Inject
  EventFirer eventFirer;

  @Incoming("gleis-in")
  void gleisabschnittChanged(byte[] msg) {
    String json = new String(msg);
    Gleisabschnitt obj = JsonbWithIncludeVisibility.SHORT.fromJson(json, Gleisabschnitt.class);
    this.logger.debugf("Received %s: %s", obj, json);
    this.eventFirer.fire(obj, Received.Literal.INSTANCE);
  }

  @Incoming("signal-in")
  void signalChanged(byte[] msg) {
    String json = new String(msg);
    Signal obj = JsonbWithIncludeVisibility.SHORT.fromJson(json, Signal.class);
    this.logger.debugf("Received %s: %s", obj, json);
    this.eventFirer.fire(obj, Received.Literal.INSTANCE);
  }

  @Incoming("weiche-in")
  void weicheChanged(byte[] msg) {
    String json = new String(msg);
    Weiche obj = JsonbWithIncludeVisibility.SHORT.fromJson(json, Weiche.class);
    this.logger.debugf("Received %s: %s", obj, json);
    this.eventFirer.fire(obj, Received.Literal.INSTANCE);
  }
}

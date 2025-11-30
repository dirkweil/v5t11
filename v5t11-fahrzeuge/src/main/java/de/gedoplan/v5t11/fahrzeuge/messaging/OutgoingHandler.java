package de.gedoplan.v5t11.fahrzeuge.messaging;

import de.gedoplan.v5t11.fahrzeuge.service.EventDispatcher;
import de.gedoplan.v5t11.util.jsf.NavigationItem;
import de.gedoplan.v5t11.util.jsonb.JsonbWithVisibility;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

/**
 * Handler für ausgehende Meldungen.
 * <p>
 * Die Methoden werden i. W. von {@link EventDispatcher} genutzt, um Veränderungen von Gleisen etc.
 * zu veröffentlichen.
 * <p>
 * Achtung: Die Methoden hierin sind nicht als Observer ausgeprägt, weil dann ein Mocking mittels {@link Alternative @Alternative}
 * nicht gelingt.
 *
 * @author dw
 */
@ApplicationScoped
public class OutgoingHandler {

  @Inject
  Logger logger;

  // Derzeit interessiert sich niemand für neue/gelöschte Fahrzeuge
  //  @Inject
  //  @Channel("fahrzeug")
  //  @OnOverflow(OnOverflow.Strategy.NONE)
  //  Emitter<String> fahrzeugEmitter;

  @Inject
  @Channel("navigation-out")
  @OnOverflow(OnOverflow.Strategy.NONE)
  Emitter<String> navigationItemEmitter;

  //  public void publish(Fahrzeug fahrzeug) {
  //    send(this.fahrzeugEmitter, fahrzeug);
  //  }

  public void publish(NavigationItem navigationItem) {
    send(this.navigationItemEmitter, navigationItem, Level.TRACE);
  }

  //  private void send(Emitter<String> emitter, Object obj) {
  //    send(emitter, obj, Level.DEBUG);
  //  }

  private void send(Emitter<String> emitter, Object obj, Level logLevel) {
    send(emitter, JsonbWithVisibility.SHORT.toJson(obj), logLevel);
  }

  protected void send(Emitter<String> emitter, String json, Level logLevel) {
    this.logger.logf(logLevel, "Send %s", json);
    emitter.send(json);
  }
}

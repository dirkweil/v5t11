package de.gedoplan.v5t11.fahrzeuge.messaging;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.jsf.NavigationItem;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;
import org.jboss.logging.Logger.Level;

/**
 * Handler für ausgehende Meldungen.
 * 
 * Die Methoden werden i. W. von {@link EventDispatcher} genutzt, um Veränderungen von Gleisen etc.
 * zu veröffentlichen.
 * 
 * Achtung: Die Methoden hierin sind nicht als Observer ausgeprägt, weil dann ein Mocking mittels {@link Alternative @Alternative}
 * nicht gelingt.
 * 
 * @author dw
 *
 */
@ApplicationScoped
public class OutgoingHandler {

  @Inject
  Logger logger;

  @Inject
  @Channel("fahrzeug")
  Emitter<String> fahrzeugEmitter;

  @Inject
  @Channel("navigation-out")
  Emitter<String> navigationItemEmitter;

  public void publish(Fahrzeug fahrzeug) {
    send(this.fahrzeugEmitter, fahrzeug);
  }

  public void publish(NavigationItem navigationItem) {
    send(this.navigationItemEmitter, navigationItem, Level.TRACE);
  }

  private void send(Emitter<String> emitter, Object obj) {
    send(emitter, obj, Level.DEBUG);
  }

  protected void send(Emitter<String> emitter, Object obj, Level logLevel) {
    String json = JsonbWithIncludeVisibility.SHORT.toJson(obj);
    this.logger.logf(logLevel, "Send %s: %s", obj, json);
    emitter.send(json);
  }

}

package de.gedoplan.v5t11.status.messaging;

import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.status.service.EventDispatcher;
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
  @Channel("status")
  Emitter<String> statusEmitter;

  @Inject
  @Channel("navigation-out")
  Emitter<String> navigationItemEmitter;

  public void publish(Fahrzeug fahrzeug) {
    send(this.statusEmitter, getStatusJson("fahrzeug", fahrzeug));
  }

  public void publish(Gleis gleis) {
    send(this.statusEmitter, getStatusJson("gleis", gleis));
  }

  public void publish(NavigationItem navigationItem) {
    send(this.navigationItemEmitter, JsonbWithIncludeVisibility.SHORT.toJson(navigationItem), Level.TRACE);
  }

  public void publish(Signal signal) {
    send(this.statusEmitter, getStatusJson("signal", signal));
  }

  public void publish(Weiche weiche) {
    send(this.statusEmitter, getStatusJson("weiche", weiche));
  }

  public void publish(Zentrale zentrale) {
    send(this.statusEmitter, getStatusJson("zentrale", zentrale));
  }

  private void send(Emitter<String> emitter, String json) {
    send(emitter, json, Level.DEBUG);
  }

  protected void send(Emitter<String> emitter, String json, Level logLevel) {
    this.logger.logf(logLevel, "Send %s", json);
    emitter.send(json);
  }

  private String getStatusJson(String name, Object value) {
    return String.format("{\"%s\":%s}", name, JsonbWithIncludeVisibility.SHORT.toJson(value));
  }

}

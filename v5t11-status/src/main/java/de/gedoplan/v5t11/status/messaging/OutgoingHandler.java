package de.gedoplan.v5t11.status.messaging;

import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleis;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeugdecoder;
import de.gedoplan.v5t11.status.service.EventDispatcher;
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

  @Inject
  @Channel("status")
  @OnOverflow(OnOverflow.Strategy.NONE)
  Emitter<String> statusEmitter;

  @Inject
  @Channel("navigation-out")
  @OnOverflow(OnOverflow.Strategy.NONE)
  Emitter<String> navigationItemEmitter;

  public void publish(Fahrzeugdecoder fahrzeugdecoder) {
    send(this.statusEmitter, getStatusJson("decoder", fahrzeugdecoder));
  }

  public void publish(Gleis gleis) {
    send(this.statusEmitter, getStatusJson("gleis", gleis));
  }

  public void publish(NavigationItem navigationItem) {
    send(this.navigationItemEmitter, JsonbWithVisibility.SHORT.toJson(navigationItem), Level.TRACE);
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
    return String.format("{\"%s\":%s}", name, JsonbWithVisibility.SHORT.toJson(value));
  }

}

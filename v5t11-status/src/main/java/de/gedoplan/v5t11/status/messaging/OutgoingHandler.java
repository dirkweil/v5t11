package de.gedoplan.v5t11.status.messaging;

import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.status.service.EventDispatcher;
import de.gedoplan.v5t11.util.domain.JoinInfo;
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
 * Die Methoden werden i. W. von {@link EventDispatcher} genutzt, um Veränderungen von Gleisabschnitten etc.
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
  @Channel("fahrzeug-status-out")
  Emitter<String> fahrzeugEmitter;

  @Inject
  @Channel("gleis-out")
  Emitter<String> gleisabschnittEmitter;

  @Inject
  @Channel("join-out")
  Emitter<String> joinInfoEmitter;

  @Inject
  @Channel("navigation-out")
  Emitter<String> navigationItemEmitter;

  @Inject
  @Channel("signal-out")
  Emitter<String> signalEmitter;

  @Inject
  @Channel("weiche-out")
  Emitter<String> weicheEmitter;

  @Inject
  @Channel("zentrale-out")
  Emitter<String> zentraleEmitter;

  public void publish(Fahrzeug fahrzeug) {
    send(this.fahrzeugEmitter, fahrzeug);
  }

  public void publish(Gleisabschnitt gleisabschnitt) {
    send(this.gleisabschnittEmitter, gleisabschnitt);
  }

  public void publish(JoinInfo joinInfo) {
    send(this.joinInfoEmitter, joinInfo);
  }

  public void publish(NavigationItem navigationItem) {
    send(this.navigationItemEmitter, navigationItem, Level.TRACE);
  }

  public void publish(Signal signal) {
    send(this.signalEmitter, signal);
  }

  public void publish(Weiche weiche) {
    send(this.weicheEmitter, weiche);
  }

  public void publish(Zentrale zentrale) {
    send(this.zentraleEmitter, zentrale);
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

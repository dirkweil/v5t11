package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

// TODO JMS -> RM

@ApplicationScoped
public class StatusPublisher {

  @Inject
  @Channel("gleis-changed")
  Emitter<String> gleisabschnittEmitter;

  @Inject
  @Channel("signal-changed")
  Emitter<String> signalEmitter;

  @Inject
  @Channel("weiche-changed")
  Emitter<String> weicheEmitter;

  @Inject
  @Channel("zentrale-changed")
  Emitter<String> zentraleEmitter;

  @Inject
  Logger logger;

  void publish(Gleisabschnitt gleisabschnitt) {
    send(this.gleisabschnittEmitter, gleisabschnitt);
  }

  void publish(Signal signal) {
    send(this.signalEmitter, signal);
  }

  void publish(Weiche weiche) {
    send(this.weicheEmitter, weiche);
  }

  void publish(Zentrale zentrale) {
    send(this.zentraleEmitter, zentrale);
  }

  protected void send(Emitter<String> emitter, Object obj) {
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("Send " + obj);
    }
    emitter.send(JsonbWithIncludeVisibility.SHORT.toJson(obj));
  }

  // void publish(@Observes Fahrzeug lok) {
  // publish(MessageCategory.LOK, lok);
  // }
  //
  // void publish(@Observes Lokcontroller lokcontroller) {
  // publish(MessageCategory.LOKCONTROLLER, lokcontroller);
  // }
  //
  // void publish(@Observes NavigationItem navigationItem) {
  // publish(MessageCategory.NAVIGATIONITEM, navigationItem);
  // }
  //
}

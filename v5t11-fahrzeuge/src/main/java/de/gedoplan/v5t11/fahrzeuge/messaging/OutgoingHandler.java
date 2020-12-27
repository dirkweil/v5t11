package de.gedoplan.v5t11.fahrzeuge.messaging;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.domain.JoinInfo;
import de.gedoplan.v5t11.util.jsf.NavigationItem;
import de.gedoplan.v5t11.util.jsonb.JsonbWithIncludeVisibility;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

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
  @Channel("fahrzeug-def-out")
  Emitter<String> fahrzeugDefEmitter;

  @Inject
  @Channel("join-out")
  Emitter<String> joinInfoEmitter;

  @Inject
  @Channel("navigation-out")
  Emitter<String> navigationItemEmitter;

  public void publish(Fahrzeug fahrzeug) {
    send(this.fahrzeugDefEmitter, fahrzeug);
  }

  public void publish(JoinInfo joinInfo) {
    send(this.joinInfoEmitter, joinInfo);
  }

  public void publish(NavigationItem navigationItem) {
    send(this.navigationItemEmitter, navigationItem);
  }

  protected void send(Emitter<String> emitter, Object obj) {
    if (this.logger.isDebugEnabled()) {
      this.logger.debug("Send " + obj);
    }
    emitter.send(JsonbWithIncludeVisibility.SHORT.toJson(obj));
  }

}

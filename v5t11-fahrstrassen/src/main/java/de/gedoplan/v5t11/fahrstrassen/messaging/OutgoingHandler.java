package de.gedoplan.v5t11.fahrstrassen.messaging;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.util.domain.JoinInfo;
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
  @Channel("join-out")
  Emitter<String> joinInfoEmitter;

  @Inject
  @Channel("fs-out")
  Emitter<String> fahrstrasseEmitter;

  public void publish(JoinInfo joinInfo) {
    send(this.joinInfoEmitter, joinInfo);
  }

  public void publish(Fahrstrasse fahrstrasse) {
    send(this.fahrstrasseEmitter, fahrstrasse);
  }

  protected void send(Emitter<String> emitter, Object obj) {
    String json = JsonbWithIncludeVisibility.SHORT.toJson(obj);
    this.logger.debugf("Send %s: %s", obj, json);
    emitter.send(json);
  }

}

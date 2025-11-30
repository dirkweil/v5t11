package de.gedoplan.v5t11.fahrstrassen.messaging;

import de.gedoplan.v5t11.fahrstrassen.entity.fahrstrasse.Fahrstrasse;
import de.gedoplan.v5t11.util.jsonb.JsonbWithVisibility;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;
import jakarta.inject.Inject;

import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.OnOverflow;
import org.jboss.logging.Logger;

/**
 * Handler für ausgehende Meldungen.
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
  @Channel("fahrstrasse")
  @OnOverflow(OnOverflow.Strategy.NONE)
  Emitter<String> fahrstrasseEmitter;

  public void publish(Fahrstrasse fahrstrasse) {
    send(this.fahrstrasseEmitter, fahrstrasse);
  }

  private void send(Emitter<String> emitter, Object obj) {
    send(emitter, JsonbWithVisibility.SHORT.toJson(obj));
  }

  protected void send(Emitter<String> emitter, String json) {
    this.logger.debugf("Send %s", json);
    emitter.send(json);
  }

}

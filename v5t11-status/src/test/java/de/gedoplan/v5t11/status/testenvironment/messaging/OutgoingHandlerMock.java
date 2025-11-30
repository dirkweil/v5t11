package de.gedoplan.v5t11.status.testenvironment.messaging;

import de.gedoplan.v5t11.status.messaging.OutgoingHandler;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger.Level;

import lombok.Getter;

@ApplicationScoped
@Alternative
@Priority(42)
@Getter
public class OutgoingHandlerMock extends OutgoingHandler {

  private String json;

  protected void send(Emitter<String> emitter, String json, Level logLevel) {
    this.json = json;
  }

}

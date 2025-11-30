package de.gedoplan.v5t11.fahrstrassen.testenvironment.messaging;

import de.gedoplan.v5t11.fahrstrassen.messaging.OutgoingHandler;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

import org.eclipse.microprofile.reactive.messaging.Emitter;

import lombok.Getter;

@ApplicationScoped
@Alternative
@Priority(42)
@Getter
public class OutgoingHandlerMock extends OutgoingHandler {

  private String json;

  @Override
  protected void send(Emitter<String> emitter, String json) {
    this.json = json;
  }

}

package de.gedoplan.v5t11.status.testenvironment.service;

import de.gedoplan.v5t11.status.service.StatusPublisher;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

import org.eclipse.microprofile.reactive.messaging.Emitter;

//TODO JMS -> RM

@ApplicationScoped
@Alternative
@Priority(1)
public class TestStatusPublisher extends StatusPublisher {

  @Override
  protected void send(Emitter<String> emitter, Object obj) {
  }

}

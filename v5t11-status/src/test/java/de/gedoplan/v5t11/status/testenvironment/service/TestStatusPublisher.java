package de.gedoplan.v5t11.status.testenvironment.service;

import de.gedoplan.v5t11.status.service.StatusPublisher;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

//TODO JMS -> RM

@ApplicationScoped
@Alternative
@Priority(1)
public class TestStatusPublisher extends StatusPublisher {

  // @Override
  // public void publish(MessageCategory category, Object status) {
  // }

}

package de.gedoplan.v5t11.status.testenvironment.service;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.jms.JMSContext;

@Dependent
public class TestJmsContextProducer {

  @Produces
  JMSContext getJmsContext() {
    return null;
  }
}

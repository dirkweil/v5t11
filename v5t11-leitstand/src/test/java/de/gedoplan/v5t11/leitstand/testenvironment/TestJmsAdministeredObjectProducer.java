package de.gedoplan.v5t11.leitstand.testenvironment;

import de.gedoplan.v5t11.leitstand.jms.JmsAdministerdObjectProducer;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.Specializes;
import javax.jms.JMSContext;
import javax.jms.Topic;

@Dependent
@Specializes
public class TestJmsAdministeredObjectProducer extends JmsAdministerdObjectProducer {

  @Produces
  Topic getTestStatusTopic() {
    return null;
  }

  @Produces
  JMSContext getTestJmsContext() {
    return null;
  }
}

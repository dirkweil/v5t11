package de.gedoplan.v5t11.fahrstrassen.testenvironment;

import de.gedoplan.v5t11.util.jms.JmsClient;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;

//TODO JMS -> RM

@ApplicationScoped
@Alternative
@Priority(1)
public class TestJmsClient extends JmsClient {
  //
  // @Override
  // public JMSConsumer getConsumer(Collection<MessageCategory> categories) {
  // throw new UnsupportedOperationException();
  // }
  //
  // @Override
  // public void send(MessageCategory category, String text) {
  // }

}

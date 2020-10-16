package de.gedoplan.v5t11.leitstand.testenvironment;

import de.gedoplan.v5t11.util.jms.JmsClient;
import de.gedoplan.v5t11.util.jms.MessageCategory;

import java.util.Collection;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.jms.JMSConsumer;

@ApplicationScoped
@Alternative
@Priority(1)
public class TestJmsClient extends JmsClient {

  @Override
  public JMSConsumer getConsumer(Collection<MessageCategory> categories) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void send(MessageCategory category, String text) {
  }

}
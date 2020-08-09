package de.gedoplan.v5t11.fahrstrassen.testenvironment;

import de.gedoplan.v5t11.fahrstrassen.gateway.JmsClient;
import de.gedoplan.v5t11.util.jms.MessageCategory;

import javax.annotation.Priority;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.jms.Message;

@ApplicationScoped
@Alternative
@Priority(1)
public class TestJmsClient extends JmsClient {

  @Override
  public Message receive() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void send(MessageCategory category, String text) {
  }

}

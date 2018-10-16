package de.gedoplan.v5t11.leitstand.testenvironment;

import de.gedoplan.v5t11.leitstand.gateway.JmsClient;
import de.gedoplan.v5t11.util.jms.MessageCategory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Specializes;
import javax.jms.Message;

@ApplicationScoped
@Specializes
public class TestJmsClient extends JmsClient {

  @Override
  public Message receive() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void send(MessageCategory category, String text) {
  }

}

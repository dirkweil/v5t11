package de.gedoplan.v5t11.strecken.service;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJBException;
import javax.ejb.MessageDriven;
import javax.inject.Inject;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.commons.logging.Log;

@MessageDriven(activationConfig = {
    @ActivationConfigProperty(propertyName = "destinationLookup", propertyValue = "jms/topic/v5t11-status"),
    @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic"),
    @ActivationConfigProperty(propertyName = "user", propertyValue = "anonymous"),
    @ActivationConfigProperty(propertyName = "password", propertyValue = "anonymous_123"),
    @ActivationConfigProperty(propertyName = "connectionParameters", propertyValue = "host=localhost;port=5445"),
    @ActivationConfigProperty(propertyName = "connectorClassName", propertyValue = "org.apache.activemq.artemis.core.remoting.impl.netty.NettyConnectorFactory")
})
public class StatusPropagator implements MessageListener {

  @Inject
  Log log;

  @Override
  public void onMessage(Message message) {
    try {
      String category = message.getStringProperty("category");
      this.log.debug("category: " + category);
      if (message instanceof TextMessage) {
        String text = ((TextMessage) message).getText();
        this.log.debug("text: " + text);
      } else {
        this.log.debug("message: " + message);
      }
    } catch (JMSException e) {
      this.log.error("Kann Message nicht verarbeiten", e);
      throw new EJBException("Kann Message nicht verarbeiten", e);
    }
  }

}

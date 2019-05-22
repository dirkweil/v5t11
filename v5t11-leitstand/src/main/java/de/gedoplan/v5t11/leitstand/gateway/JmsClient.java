package de.gedoplan.v5t11.leitstand.gateway;

import de.gedoplan.baselibs.naming.JNDIContextFactory;
import de.gedoplan.baselibs.naming.LookupHelper;
import de.gedoplan.v5t11.leitstand.service.ConfigService;
import de.gedoplan.v5t11.util.jms.MessageCategory;

import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.DeliveryMode;
import javax.jms.JMSConsumer;
import javax.jms.JMSContext;
import javax.jms.JMSRuntimeException;
import javax.jms.Message;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;

@ApplicationScoped
public class JmsClient {

  private static final MessageCategory[] CATEGORIES = {
      MessageCategory.FAHRSTRASSE,
      MessageCategory.GLEIS,
      MessageCategory.LOKCONTROLLER,
      MessageCategory.SIGNAL,
      MessageCategory.WEICHE,
      MessageCategory.ZENTRALE
  };

  @Inject
  ConfigService configService;

  @Inject
  Log log;

  private JMSContext jmsContext;
  private Topic topic;
  private JMSConsumer consumer;

  public Message receive() {
    if (this.jmsContext == null) {
      init();
    }

    try {
      return this.consumer.receive();
    } catch (JMSRuntimeException e) {
      reset();
      throw e;
    }
  }

  public void send(MessageCategory category, String text) {
    if (this.jmsContext == null) {
      init();
    }

    try {
      this.jmsContext
          .createProducer()
          .setTimeToLive(10 * 1000)
          .setDeliveryMode(DeliveryMode.NON_PERSISTENT)
          .setProperty("category", category.toString())
          .send(this.topic, text);
    } catch (JMSRuntimeException e) {
      reset();
      throw e;
    }

  }

  private void init() {

    Context jndiContext = null;
    ConnectionFactory connectionFactory = null;
    try {
      String statusJmsUrl = this.configService.getStatusJmsUrl();

      if (this.log.isDebugEnabled()) {
        this.log.debug("JMS URL: " + statusJmsUrl);
      }

      if (statusJmsUrl.startsWith("http-remoting")) {

        // Verbindung über WildFly
        jndiContext = JNDIContextFactory.getInitialContext(this.configService.getStatusJmsUrl(), null, null);
        connectionFactory = (ConnectionFactory) jndiContext.lookup(LookupHelper.getDefaultJmsConnectionFactoryLookupName());

      } else {

        // Direkte Verbindung zu Artemis
        Properties prop = new Properties();
        prop.setProperty("java.naming.factory.initial", "org.apache.activemq.artemis.jndi.ActiveMQInitialContextFactory");
        prop.setProperty("connectionFactory.ConnectionFactory", statusJmsUrl);
        prop.setProperty("topic.jms/topic/v5t11-status", "jms.topic.v5t11-status");

        jndiContext = new InitialContext(prop);
        connectionFactory = (ConnectionFactory) jndiContext.lookup("ConnectionFactory");
      }

      this.topic = (Topic) jndiContext.lookup("jms/topic/v5t11-status");

      String selector = Stream.of(CATEGORIES)
          .map(Object::toString)
          .collect(Collectors.joining("','", "category in ('", "')"));

      if (this.log.isTraceEnabled()) {
        this.log.trace("jndiContext: " + jndiContext);
        this.log.trace("connectionFactory: " + connectionFactory);
        this.log.trace("topic: " + this.topic);
        this.log.trace("selector: " + selector);
      }

      this.jmsContext = connectionFactory.createContext("anonymous", "anonymous_123");

      this.consumer = this.jmsContext.createConsumer(this.topic, selector);
    } catch (NamingException e) {
      throw new JMSRuntimeException("Cannot connect to JNDI", null, e);
    } finally {
      try {
        jndiContext.close();
      } catch (Exception e) {
      }
    }
  }

  private void reset() {
    if (this.jmsContext != null) {
      try {
        this.jmsContext.close();
      } catch (Exception e) {
      }
    }

    this.jmsContext = null;
    this.consumer = null;
  }

}

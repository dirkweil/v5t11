package de.gedoplan.v5t11.fahrstrassen.jms;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Disposes;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.Topic;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.logging.Log;

@ApplicationScoped
public class JmsAdministerdObjectProducer {

  private Context ic;
  private ConnectionFactory cf;
  private Topic statusTopic;

  @Inject
  Log log;

  @PostConstruct
  void postConstruct() {
    try {
      this.ic = new InitialContext();
      this.cf = (ConnectionFactory) this.ic.lookup("ConnectionFactory");
      this.statusTopic = (Topic) this.ic.lookup("jms/topic/v5t11-status");

      if (this.log.isDebugEnabled()) {
        this.log.debug("jndiContext: " + this.ic);
        this.log.debug("connectionFactory: " + this.cf);
        this.log.debug("statusTopic: " + this.statusTopic);
      }
    } catch (NamingException e) {
      throw new IllegalStateException("Kann JMS-Objekte nicht finden", e);
    }
  }

  @PreDestroy
  void preDestroy() {
    try {
      if (this.log.isDebugEnabled()) {
        this.log.debug("close jndiContext");
      }
      this.ic.close();
    } catch (NamingException e) {
      // ignore
    }
  }

  @Produces
  JMSContext createJmsContext() {
    JMSContext jmsContext = this.cf.createContext("anonymous", "anonymous_123");

    if (this.log.isDebugEnabled()) {
      this.log.debug("produced jmsContext: " + jmsContext);
    }

    return jmsContext;
  }

  void closeJmsContext(@Disposes JMSContext jmsContext) {
    if (this.log.isDebugEnabled()) {
      this.log.debug("dispose jmsContext");
    }

    jmsContext.close();
  }

  @Produces
  Topic getStatusTopic() {
    return this.statusTopic;
  }

}

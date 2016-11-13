package de.gedoplan.v5t11.betriebssteuerung.messaging;

import java.io.Serializable;

import javax.enterprise.context.ApplicationScoped;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@ApplicationScoped
public class MessagePusher implements Serializable
{
  private static final String CONNECTION_FACTORY = "java:/ConnectionFactory";
  private static final String TOPIC              = "jms/topic/V5T11SelectrixMessage";

  private static final Log    LOG                = LogFactory.getLog(MessagePusher.class);

  public void pushMessage(Serializable message)
  {
    if (LOG.isDebugEnabled())
    {
      LOG.debug("Push message: " + message);
    }

    Connection conn = null;
    try
    {
      // Verbindung zum JNDI aufbauen
      Context jndiCtx = new InitialContext();

      // Connection-Factory ermitteln und JMS-Verbindung erzeugen
      ConnectionFactory connFactory = (ConnectionFactory) jndiCtx.lookup(CONNECTION_FACTORY);
      conn = connFactory.createConnection();
      conn.start();

      // JMS-Session erzeugen
      Session sess = conn.createSession(false, Session.AUTO_ACKNOWLEDGE);

      // Destination oeffnen
      Destination dest = (Destination) PortableRemoteObject.narrow(jndiCtx.lookup(TOPIC), Destination.class);

      // Producer und Meldung erzeugen
      MessageProducer producer = sess.createProducer(dest);
      ObjectMessage objectMessage = sess.createObjectMessage(message);

      // Meldung senden
      producer.send(objectMessage);
    }
    catch (Exception e)
    {
      LOG.error("Cannot push message to JMS", e);
    }
    finally
    {
      try
      {
        if (conn != null)
        {
          conn.close();
        }
      }
      catch (JMSException e)
      {
        // ignore
      }
    }
  }
}

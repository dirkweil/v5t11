package de.gedoplan.v5t11.stellwerk;

import de.gedoplan.baselibs.utils.remote.ServiceLocator;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.ObjectMessage;
import javax.jms.Session;

public class JmsTester
{
  public static void main(String[] args) throws Exception
  {
    ConnectionFactory connectionFactory = ServiceLocator.getService(ConnectionFactory.class, "jms/RemoteConnectionFactory");
    System.out.println(connectionFactory);

    Destination destination = ServiceLocator.getService(Destination.class, "jms/topic/test");
    System.out.println(destination);

    Connection connection = connectionFactory.createConnection("anonymous", "anonymousanonymous");

    // JMS-Session erzeugen
    Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

    // Consumer erzeugen
    MessageConsumer consumer = session.createConsumer(destination);

    // Verbindung starten
    connection.start();

    while (true)
    {
      Message msg = consumer.receive();
      System.out.println(msg.getClass().getName());
      if (msg instanceof ObjectMessage)
      {
        ObjectMessage objectMessage = (ObjectMessage) msg;
        System.out.println(objectMessage.getObject());
      }
    }

  }

}

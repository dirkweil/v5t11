package de.gedoplan;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSContext;
import javax.naming.InitialContext;

public class TestReceice {

  public static void main(String[] args) throws Exception {
    InitialContext ic = new InitialContext();

    ConnectionFactory cf = (ConnectionFactory) ic.lookup("ConnectionFactory");

    // And look up the Queue:

    Destination destination = (Destination) ic.lookup("jms/topic/v5t11-status");

    System.out.println("cf: " + cf);
    System.out.println("destination: " + destination);

    try (JMSContext jmsContext = cf.createContext("anonymous", "anonymous_123")) {

      System.out.println("jmsContext: " + jmsContext);

      while (true) {
        System.out.println(jmsContext.createConsumer(destination).receiveBody(String.class, 1000));
      }
    }
  }

}

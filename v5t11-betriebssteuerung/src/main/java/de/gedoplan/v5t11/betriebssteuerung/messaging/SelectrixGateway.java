package de.gedoplan.v5t11.betriebssteuerung.messaging;

import de.gedoplan.v5t11.betriebssteuerung.qualifier.Outbound;
import de.gedoplan.v5t11.selectrix.SelectrixConnection;
import de.gedoplan.v5t11.selectrix.SelectrixConnectionFactory;
import de.gedoplan.v5t11.selectrix.SelectrixMessage;

import java.util.Collection;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class SelectrixGateway {
  @Resource(mappedName = "java:/eis/SelectrixConnectionFactory")
  private SelectrixConnectionFactory connectionFactory;

  private SelectrixConnection connection;

  @PostConstruct
  public void init() {
    this.connection = this.connectionFactory.getConnection();
  }

  @PreDestroy
  public void cleanup() {
    try {
      this.connection.close();
    } catch (Exception ignore) {
    }
  }

  public void start(String serialPortName, int serialPortSpeed, String interfaceName, Collection<Integer> adressen) {
    this.connection.start(serialPortName, serialPortSpeed, interfaceName, adressen);
  }

  public void stop() {
    this.connection.stop();
  }

  public int getValue(int address) {
    return this.connection.getValue(address);
  }

  public int getValue(int address, boolean refresh) {
    return this.connection.getValue(address, refresh);
  }

  public void setValue(int address, int value) {
    this.connection.setValue(address, value);
  }

  public void setValue(@Observes @Outbound SelectrixMessage selectrixMessage) {
    setValue(selectrixMessage.getAddress(), selectrixMessage.getValue());
  }
}

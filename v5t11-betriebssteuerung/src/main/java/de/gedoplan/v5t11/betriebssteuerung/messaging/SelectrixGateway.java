package de.gedoplan.v5t11.betriebssteuerung.messaging;

import de.gedoplan.v5t11.betriebssteuerung.qualifier.Outbound;
import de.gedoplan.v5t11.selectrix.SelectrixConnection;
import de.gedoplan.v5t11.selectrix.SelectrixConnectionFactory;
import de.gedoplan.v5t11.selectrix.SelectrixMessage;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@ApplicationScoped
public class SelectrixGateway
{
  @Resource(mappedName = "java:/eis/SelectrixConnectionFactory")
  private SelectrixConnectionFactory connectionFactory;

  private SelectrixConnection        connection;

  @PostConstruct
  public void init()
  {
    this.connection = this.connectionFactory.getConnection();
  }

  @PreDestroy
  public void cleanup()
  {
    try
    {
      this.connection.close();
    }
    catch (Exception ignore)
    {
    }
  }

  public void addAddressen(int... adressen)
  {
    List<Integer> al = new ArrayList<>();
    for (int a : adressen)
    {
      al.add(a);
    }
    this.connection.addAdressen(al);

  }

  public void addAddressen(List<Integer> adressen)
  {
    this.connection.addAdressen(adressen);
    try
    {
      Thread.sleep(500);
    }
    catch (Exception e)
    {
      // ignore
    }
  }

  public int getValue(int address)
  {
    return this.connection.getValue(address);
  }

  public int getValue(int address, boolean refresh)
  {
    return this.connection.getValue(address, refresh);
  }

  public void setValue(int address, int value)
  {
    this.connection.setValue(address, value);
  }

  public void setValue(@Observes @Outbound SelectrixMessage selectrixMessage)
  {
    setValue(selectrixMessage.getAddress(), selectrixMessage.getValue());
  }
}

package de.gedoplan.v5t11.selectrix.impl;

import gnu.io.CommPortIdentifier;

import java.util.Enumeration;

/**
 * @author Dirk Weil
 */
public class ListPorts
{

  public static void main(String[] args) throws Exception
  {
    showAll();
  }

  public static void showAll()
  {
    @SuppressWarnings("unchecked")
    Enumeration<CommPortIdentifier> portList = CommPortIdentifier.getPortIdentifiers();
    while (portList.hasMoreElements())
    {
      CommPortIdentifier portId = portList.nextElement();
      System.out.println("Available port: " + portId.getName() + " (" + getPortTypeName(portId.getPortType()) + ")");
    }
  }

  private static String getPortTypeName(int portType)
  {
    switch (portType)
    {
    case CommPortIdentifier.PORT_I2C:
      return "I2C";
    case CommPortIdentifier.PORT_PARALLEL:
      return "Parallel";
    case CommPortIdentifier.PORT_RAW:
      return "Raw";
    case CommPortIdentifier.PORT_RS485:
      return "RS485";
    case CommPortIdentifier.PORT_SERIAL:
      return "Serial";
    default:
      return "unknown type";
    }
  }

}

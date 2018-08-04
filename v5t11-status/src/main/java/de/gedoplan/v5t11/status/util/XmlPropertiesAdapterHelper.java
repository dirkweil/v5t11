package de.gedoplan.v5t11.status.util;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class XmlPropertiesAdapterHelper
{
  @XmlElement(name = "property")
  public ArrayList<Property> properties;

  public static class Property
  {
    @XmlAttribute
    public String name;
    @XmlAttribute
    public String value;
  }
}

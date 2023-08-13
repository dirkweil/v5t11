package de.gedoplan.v5t11.util.jaxb;

import java.util.ArrayList;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;

public class XmlPropertiesAdapterHelper {
  @XmlElement(name = "property")
  public ArrayList<Property> properties;

  public static class Property {
    @XmlAttribute
    public String name;
    @XmlAttribute
    public String value;
  }
}

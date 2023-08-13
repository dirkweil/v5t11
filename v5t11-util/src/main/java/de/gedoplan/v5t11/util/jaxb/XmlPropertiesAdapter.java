package de.gedoplan.v5t11.util.jaxb;

import de.gedoplan.v5t11.util.jaxb.XmlPropertiesAdapterHelper.Property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import jakarta.xml.bind.annotation.adapters.XmlAdapter;

public class XmlPropertiesAdapter extends XmlAdapter<XmlPropertiesAdapterHelper, Map<String, String>> {

  @Override
  public Map<String, String> unmarshal(XmlPropertiesAdapterHelper helper) throws Exception {
    if (helper.properties == null || helper.properties.isEmpty()) {
      return null;
    }

    Map<String, String> properties = new HashMap<>();
    for (Property entry : helper.properties) {
      properties.put(entry.name, entry.value);
    }
    return properties;
  }

  @Override
  public XmlPropertiesAdapterHelper marshal(Map<String, String> properties) throws Exception {
    if (properties == null) {
      return null;
    }

    XmlPropertiesAdapterHelper helper = new XmlPropertiesAdapterHelper();
    helper.properties = new ArrayList<>();
    for (Entry<String, String> entry : properties.entrySet()) {
      Property property = new Property();
      property.name = entry.getKey();
      property.value = entry.getValue();
      helper.properties.add(property);
    }
    return helper;
  }

}

package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.util.config.ConfigBase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConfigService extends ConfigBase {

  public static final String PROPERTY_IF_TYP = "v5t11.ifTyp";
  public static final String PROPERTY_PORT_NAME = "v5t11.portName";
  public static final String PROPERTY_PORT_SPEED = "v5t11.portSpeed";

  public static final String DEFAULT_IF_TYP = "rautenhaus";
  public static final String DEFAULT_PORT_NAME = "auto";

  public String getPortName() {
    return getProperty(PROPERTY_PORT_NAME, DEFAULT_PORT_NAME);
  }

  public int getPortSpeed() {
    return Integer.parseInt(getProperty(PROPERTY_PORT_SPEED, "0"));
  }

}

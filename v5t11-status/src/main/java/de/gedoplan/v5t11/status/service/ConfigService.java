package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.util.config.ConfigBase;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;

@ApplicationScoped
public class ConfigService extends ConfigBase {

  public static final String PROPERTY_IF_TYP = "v5t11.ifTyp";
  public static final String PROPERTY_PORT_NAME = "v5t11.portName";
  public static final String PROPERTY_PORT_SPEED = "v5t11.portSpeed";

  public static final String DEFAULT_IF_TYP = "rautenhaus";
  public static final String DEFAULT_PORT_NAME = "auto";

  @Inject
  @ConfigProperty(name = PROPERTY_PORT_NAME, defaultValue = DEFAULT_PORT_NAME)
  @Getter
  String portName;

  @Inject
  @ConfigProperty(name = PROPERTY_PORT_SPEED, defaultValue = "0")
  @Getter
  int portSpeed;

}

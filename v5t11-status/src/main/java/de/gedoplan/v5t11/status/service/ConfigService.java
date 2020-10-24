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

  public static final String PROPERTY_STATUS_WEB_URL = "v5t11.status/web/url";
  public static final String DEFAULT_STATUS_WEB_URL = "http://v5t11-status:8080";

  @Inject
  @ConfigProperty(name = PROPERTY_PORT_NAME, defaultValue = DEFAULT_PORT_NAME)
  @Getter
  String portName;

  @Inject
  @ConfigProperty(name = PROPERTY_PORT_SPEED, defaultValue = "0")
  @Getter
  int portSpeed;

  @Inject
  @ConfigProperty(name = PROPERTY_STATUS_WEB_URL, defaultValue = DEFAULT_STATUS_WEB_URL)
  @Getter
  String statusWebUrl;
}

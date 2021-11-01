package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.util.config.ConfigBase;


import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import lombok.Getter;

@ApplicationScoped
public class ConfigService extends ConfigBase {

  public static final String PROPERTY_IF_TYP = "v5t11.ifTyp";
  public static final String PROPERTY_PORT_NAME = "v5t11.portName";
  public static final String PROPERTY_PORT_SPEED = "v5t11.portSpeed";

  public static final String DEFAULT_IF_TYP = "rautenhaus";

  public static final String PROPERTY_STATUS_WEB_URL = "v5t11.status/web/url";

  @Inject
  Logger logger;

  @PostConstruct
  void postConstruct() {
    logger.infof("Initialized ConfigService; statusWebUrl=" + statusWebUrl);
  }

  @Inject
  @ConfigProperty(name = PROPERTY_PORT_NAME)
  @Getter
  String portName;

  @Inject
  @ConfigProperty(name = PROPERTY_PORT_SPEED)
  @Getter
  int portSpeed;

  @Inject
  @ConfigProperty(name = PROPERTY_STATUS_WEB_URL)
  @Getter
  String statusWebUrl;
}

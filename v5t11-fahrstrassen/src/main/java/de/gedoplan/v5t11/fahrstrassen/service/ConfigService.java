package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.util.config.ConfigBase;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ConfigService extends ConfigBase {

  public static final String PROPERTY_STATUS_REST_URL = "v5t11.statusRestUrl";
  public static final String DEFAULT_STATUS_REST_URL = "http://v5t11-status:8080/rs";

  public static final String PROPERTY_STATUS_JMS_URL = "v5t11.statusJmsUrl";
  public static final String DEFAULT_STATUS_JMS_URL = "tcp://v5t11-status:5445";

  public String getStatusRestUrl() {
    return getProperty(PROPERTY_STATUS_REST_URL, DEFAULT_STATUS_REST_URL);
  }

  public String getStatusJmsUrl() {
    return getProperty(PROPERTY_STATUS_JMS_URL, DEFAULT_STATUS_JMS_URL);
  }

}

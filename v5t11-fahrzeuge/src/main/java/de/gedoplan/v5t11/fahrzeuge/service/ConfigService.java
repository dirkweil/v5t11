package de.gedoplan.v5t11.fahrzeuge.service;

import de.gedoplan.v5t11.util.config.ConfigBase;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;

@ApplicationScoped
public class ConfigService extends ConfigBase {

  public static final String PROPERTY_FAHRZEUGE_WEB_URL = "v5t11.fahrzeuge/web/url";
  public static final String DEFAULT_FAHRZEUGE_WEB_URL = "http://v5t11-fahrzeuge:8080";

  @Inject
  @ConfigProperty(name = PROPERTY_FAHRZEUGE_WEB_URL, defaultValue = DEFAULT_FAHRZEUGE_WEB_URL)
  @Getter
  String fahrzeugeWebUrl;
}

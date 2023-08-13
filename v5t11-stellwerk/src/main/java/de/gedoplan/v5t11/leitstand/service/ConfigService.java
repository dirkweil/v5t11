package de.gedoplan.v5t11.leitstand.service;

import de.gedoplan.v5t11.util.config.ConfigBase;

import lombok.Getter;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class ConfigService extends ConfigBase {

  public static final String PROPERTY_LEITSTAND_WEB_URL = "v5t11.leitstand/web/url";
  public static final String DEFAULT_LEITSTAND_WEB_URL = "http://v5t11-leitstand:8080";

  @Inject
  @ConfigProperty(name = PROPERTY_LEITSTAND_WEB_URL, defaultValue = DEFAULT_LEITSTAND_WEB_URL)
  @Getter
  String leitstandWebUrl;
}

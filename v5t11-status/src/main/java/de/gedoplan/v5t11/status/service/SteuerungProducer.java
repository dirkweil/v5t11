package de.gedoplan.v5t11.status.service;

import de.gedoplan.baselibs.utils.inject.InjectionUtil;
import de.gedoplan.baselibs.utils.xml.XmlConverter;
import de.gedoplan.v5t11.status.entity.Steuerung;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.CreationException;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;

@ApplicationScoped
public class SteuerungProducer {

  public static final String V5T11_CONFIG = "v5t11.config";

  @Inject
  SelectrixGateway selectrixGateway;

  @Produces
  @ApplicationScoped
  Steuerung createSteuerung() {

    String config = System.getProperty(V5T11_CONFIG);
    if (config == null) {
      throw new IllegalArgumentException("System Property " + V5T11_CONFIG + " nicht gesetzt");
    }

    String configFile = config;
    if (!configFile.endsWith(".xml")) {
      configFile += "_sx.xml";
    }

    Steuerung steuerung;

    try {
      Path configPath = Paths.get(configFile);
      if (Files.exists(configPath)) {
        try (InputStream is = new FileInputStream(configPath.toFile());
            Reader reader = new InputStreamReader(is, "UTF-8")) {
          steuerung = XmlConverter.fromXml(Steuerung.class, reader);
        }
      } else {
        steuerung = XmlConverter.fromXml(Steuerung.class, configFile);
      }
      InjectionUtil.injectFields(steuerung);
    } catch (Exception e) {
      throw new CreationException("Kann Konfiguration " + config + " nicht lesen", e);
    }

    return steuerung;
  }

}

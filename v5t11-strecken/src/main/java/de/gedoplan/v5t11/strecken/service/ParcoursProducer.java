package de.gedoplan.v5t11.strecken.service;

import de.gedoplan.baselibs.utils.xml.XmlConverter;
import de.gedoplan.v5t11.strecken.entity.Parcours;

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

@ApplicationScoped
public class ParcoursProducer {

  public static final String CONFIG = "v5t11.strecken.config";

  @Produces
  @ApplicationScoped
  Parcours createSteuerung() {

    String config = System.getProperty(CONFIG);
    if (config == null) {
      throw new IllegalArgumentException("System Property " + CONFIG + " nicht gesetzt");
    }

    String configFile = config;
    if (!configFile.endsWith(".xml")) {
      configFile += "_parcours.xml";
    }

    try {
      Path configPath = Paths.get(configFile);
      if (Files.exists(configPath)) {
        try (InputStream is = new FileInputStream(configPath.toFile());
            Reader reader = new InputStreamReader(is, "UTF-8")) {
          return XmlConverter.fromXml(Parcours.class, reader);
        }
      } else {
        return XmlConverter.fromXml(Parcours.class, configFile);
      }
    } catch (Exception e) {
      throw new CreationException("Kann Konfiguration " + config + " nicht lesen", e);
    }
  }

}

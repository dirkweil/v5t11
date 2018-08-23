package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.baselibs.utils.xml.XmlConverter;
import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Gleisabschnitt;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Signal;
import de.gedoplan.v5t11.fahrstrassen.entity.fahrweg.Weiche;
import de.gedoplan.v5t11.fahrstrassen.gateway.GleisResourceClient;
import de.gedoplan.v5t11.fahrstrassen.gateway.SignalResourceClient;
import de.gedoplan.v5t11.fahrstrassen.gateway.WeicheResourceClient;

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
  Parcours createParcours(GleisResourceClient gleisResourceClient, SignalResourceClient signalResourceClient, WeicheResourceClient weicheResourceClient) {

    String config = System.getProperty(CONFIG);
    if (config == null) {
      throw new IllegalArgumentException("System Property " + CONFIG + " nicht gesetzt");
    }

    String configFile = config;
    if (!configFile.endsWith(".xml")) {
      configFile += "_parcours.xml";
    }

    try {
      Parcours parcours;

      Path configPath = Paths.get(configFile);
      if (Files.exists(configPath)) {
        try (InputStream is = new FileInputStream(configPath.toFile());
            Reader reader = new InputStreamReader(is, "UTF-8")) {
          parcours = XmlConverter.fromXml(Parcours.class, reader);
        }
      } else {
        parcours = XmlConverter.fromXml(Parcours.class, configFile);
      }

      // Aktuelle Zustände von Gleisabschnitten, Weichen und Signalen holen
      gleisResourceClient.getGleisabschnitte().forEach(other -> {
        Gleisabschnitt gleisabschnitt = parcours.getGleisabschnitt(other.getBereich(), other.getName());
        if (gleisabschnitt != null) {
          gleisabschnitt.copyStatus(other);
        }
      });
      signalResourceClient.getSignale().forEach(other -> {
        Signal signal = parcours.getSignal(other.getBereich(), other.getName());
        if (signal != null) {
          signal.copyStatus(other);
        }
      });
      weicheResourceClient.getWeichen().forEach(other -> {
        Weiche weiche = parcours.getWeiche(other.getBereich(), other.getName());
        if (weiche != null) {
          weiche.copyStatus(other);
        }
      });

      // Fahrstrassen komplettieren
      parcours.completeFahrstrassen();

      return parcours;
    } catch (Exception e) {
      throw new CreationException("Kann Konfiguration " + config + " nicht lesen", e);
    }
  }

}

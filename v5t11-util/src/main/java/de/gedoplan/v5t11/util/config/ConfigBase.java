package de.gedoplan.v5t11.util.config;

import de.gedoplan.baselibs.utils.xml.XmlConverter;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.enterprise.inject.CreationException;
import javax.inject.Inject;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import lombok.Getter;

/**
 * Basisklasse für Konfigurationsservices in den einzelnen V5T11-Anwendungen.
 *
 * Diese Klasse soll als Basisklasse für @ApplicationScoped-Beans genutzt werden, die die
 * anwendungsspezifischen Konfigurationswerte bereit stellen und sich dabei der übergreifenden
 * Funktionen dieser Klasse bedienen:
 * - Alle Werte können als Environment-Variablen, System-Properties oder Einträge in v5t11.properties gesetzt werden.
 * - Konfigurationsdateien werden im mittels {@link #PROPERTY_CONFIG_DIR} angegebenen Verzeichnis gesucht; Default ist das Home-Verzeichnis des Users.
 * - Der Name der angeschlossenen Anlage wird über die Property {@link #PROPERTY_ANLAGE} spezifiziert; Default ist {@link #DEFAULT_ANLAGE}.
 *
 * @author dw
 *
 */
public abstract class ConfigBase {

  public static final String PROPERTY_CONFIG_DIR = "v5t11.configDir";

  public static final String PROPERTY_ARTIFACT_ID = "v5t11.artifactId";
  public static final String PROPERTY_VERSION = "v5t11.version";

  public static final String PROPERTY_ANLAGE = "v5t11.anlage";
  public static final String DEFAULT_ANLAGE = "show";

  public static final String PROPERTY_STATUS_REST_URL = "v5t11.v5t11-status/mp-rest/url";
  public static final String DEFAULT_STATUS_REST_URL = "http://v5t11-status:8080/rs";

  public static final String PROPERTY_STATUS_JMS_URL = "v5t11.statusJmsUrl";
  public static final String DEFAULT_STATUS_JMS_URL = "tcp://v5t11-status:5445";
  // public static final String DEFAULT_STATUS_JMS_URL = "http-remoting://v5t11-status:8080";

  public static final String PROPERTY_FAHRSTRASSEN_REST_URL = "v5t11.fahrstrassenRestUrl";
  public static final String DEFAULT_FAHRSTRASSEN_REST_URL = "http://v5t11-fahrstrassen:8080/rs";

  @Inject
  @ConfigProperty(name = PROPERTY_CONFIG_DIR)
  @Getter
  String configDir;

  @Inject
  @ConfigProperty(name = PROPERTY_ANLAGE, defaultValue = DEFAULT_ANLAGE)
  @Getter
  String anlage;

  @Inject
  @ConfigProperty(name = PROPERTY_ARTIFACT_ID)
  @Getter
  String artifactId;

  @Inject
  @ConfigProperty(name = PROPERTY_VERSION)
  @Getter
  String version;

  @Inject
  @ConfigProperty(name = PROPERTY_STATUS_REST_URL, defaultValue = DEFAULT_STATUS_REST_URL)
  @Getter
  String statusRestUrl;

  @Inject
  @ConfigProperty(name = PROPERTY_FAHRSTRASSEN_REST_URL, defaultValue = DEFAULT_FAHRSTRASSEN_REST_URL)
  @Getter
  String fahrstrassenRestUrl;

  /**
   * XML-Konfigurationsfile lesen und deserialisieren.
   *
   * Der Dateiname setzt sich aus dem Anwendungsnamen und dem übergebenen Suffix zusammen. Die Datei wird im Konfigurationsverzeichnis oder als Classpath Ressource gesucht.
   *
   * @param fileNameSuffix
   *          Dateinamen-Suffix
   * @param clazz
   *          Ziel-Klasse
   * @return deserialisierter Wert
   */
  public <T> T readXmlConfig(String fileNameSuffix, Class<T> clazz) {
    String fileName = this.anlage + fileNameSuffix;

    try {
      // Wenn vorhanden, Datei aus V5T11-Konfigurationsverzeichnis lesen
      Path path = Paths.get(this.configDir, fileName);
      if (Files.exists(path)) {
        try (Reader reader = new FileReader(path.toFile())) {
          return XmlConverter.fromXml(clazz, reader);
        }
      }

      // Andernfalls Classpath-Ressource lesen
      return XmlConverter.fromXml(clazz, fileName);

    } catch (Exception e) {
      throw new CreationException("Kann Konfiguration " + fileName + " nicht lesen", e);
    }
  }

}

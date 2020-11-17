package de.gedoplan.v5t11.util.config;

import de.gedoplan.baselibs.utils.exception.BugException;
import de.gedoplan.baselibs.utils.util.ResourceUtil;
import de.gedoplan.baselibs.utils.xml.XmlConverter;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.net.URISyntaxException;
import java.net.URL;
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

  public static final String PROPERTY_STATUS_REST_URL = "v5t11.status/mp-rest/url";
  public static final String DEFAULT_STATUS_REST_URL = "http://v5t11-status:8080/rs";

  public static final String PROPERTY_STATUS_JMS_URL = "v5t11.status/jms/url";
  public static final String DEFAULT_STATUS_JMS_URL = "http-remoting://v5t11-status:8080";

  public static final String PROPERTY_FAHRSTRASSEN_REST_URL = "v5t11.fahrstrassen/mp-rest/url";
  public static final String DEFAULT_FAHRSTRASSEN_REST_URL = "http://v5t11-fahrstrassen:8080";

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
  @ConfigProperty(name = PROPERTY_STATUS_JMS_URL, defaultValue = DEFAULT_STATUS_JMS_URL)
  @Getter
  String statusJmsUrl;

  @Inject
  @ConfigProperty(name = PROPERTY_FAHRSTRASSEN_REST_URL, defaultValue = DEFAULT_FAHRSTRASSEN_REST_URL)
  @Getter
  String fahrstrassenRestUrl;

  /**
   * Veränderungs-Zeit der XML-Konfigurationsdatei ermitteln.
   * 
   * @param fileNameSuffix Dateinamen-Suffix
   * @return Letzte Änderung in ms seit 1970.
   */
  public long getXmlConfigLastModified(String fileNameSuffix) {
    File xmlConfigFile = getXmlConfigFile(fileNameSuffix);
    return xmlConfigFile.lastModified();
  }

  /**
   * XML-Konfigurationsfile lesen und deserialisieren.
   *
   * Der Dateiname setzt sich aus dem Anwendungsnamen und dem übergebenen Suffix zusammen. Die Datei wird im Konfigurationsverzeichnis oder als Classpath Ressource gesucht.
   *
   * @param fileNameSuffix Dateinamen-Suffix
   * @param clazz Ziel-Klasse
   * @return deserialisierter Wert
   */
  public <T> T readXmlConfig(String fileNameSuffix, Class<T> clazz) {
    File xmlConfigFile = getXmlConfigFile(fileNameSuffix);

    try (Reader reader = new FileReader(xmlConfigFile)) {
      return XmlConverter.fromXml(clazz, reader);
    } catch (Exception e) {
      throw new CreationException("Kann Konfiguration " + xmlConfigFile.getAbsolutePath() + " nicht lesen", e);
    }
  }

  private File getXmlConfigFile(String fileNameSuffix) {
    String fileName = this.anlage + fileNameSuffix;
    Path path = Paths.get(this.configDir, fileName);
    if (Files.exists(path)) {
      return path.toFile();
    }

    URL resourceURL = ResourceUtil.getResource(fileName);
    if (resourceURL != null) {
      try {
        return new File(resourceURL.toURI());
      } catch (URISyntaxException e) {
        throw new BugException(e);
      }
    }

    throw new IllegalArgumentException("Konfiguration " + fileName + " nicht gefunden");
  }

}

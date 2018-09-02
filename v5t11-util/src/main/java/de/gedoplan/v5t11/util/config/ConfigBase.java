package de.gedoplan.v5t11.util.config;

import de.gedoplan.baselibs.utils.xml.XmlConverter;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.enterprise.inject.CreationException;

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

  public static final String PROPERTY_ANLAGE = "v5t11.anlage";
  public static final String PROPERTY_CONFIG_DIR = "v5t11.configDir";

  public static final String DEFAULT_ANLAGE = "show";

  private Properties v5t11Properties;

  public ConfigBase() {
    this.v5t11Properties = new Properties();
    try (Reader reader = new FileReader(getConfigDir() + "/v5t11.properties")) {
      this.v5t11Properties.load(reader);
    } catch (Exception e) {
      // ignore
    }
  }

  /**
   * Konfigurationswert liefern.
   * 
   * @param name
   *          Name der Property
   * @param defaultValue
   *          Defaultwert
   * @return gefundener Wert in dieser Reihenfolge: Environment Variable, System Property, Eintrag aus v5t11.properties, Defaultwert
   */
  public String getProperty(String name, String defaultValue) {
    String value = System.getenv(name);
    if (value == null) {
      value = System.getProperty(name);
    }
    if (value == null) {
      value = this.v5t11Properties.getProperty(name);
    }
    if (value == null) {
      value = defaultValue;
    }
    return value;
  }

  /**
   * Konfigurationsverzeichnis liefern.
   * 
   * @return Verzeichnis
   */
  public String getConfigDir() {
    String configDir = getProperty(PROPERTY_CONFIG_DIR, null);
    if (configDir == null) {
      configDir = System.getProperty("user.home") + "/v5t11";
    }
    return configDir;
  }

  /**
   * Anlagenname liefern.
   * 
   * @return Anlagenname
   */
  public String getAnlage() {
    return getProperty(PROPERTY_ANLAGE, DEFAULT_ANLAGE);
  }

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
    String fileName = getAnlage() + fileNameSuffix;

    try {
      // Wenn vorhanden, Datei aus V5T11-Konfigurationsverzeichnis lesen
      Path path = Paths.get(getConfigDir(), fileName);
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

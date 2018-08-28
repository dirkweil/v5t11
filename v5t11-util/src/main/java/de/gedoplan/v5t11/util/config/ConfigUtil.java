package de.gedoplan.v5t11.util.config;

import de.gedoplan.baselibs.utils.xml.XmlConverter;

import java.io.FileReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import javax.enterprise.inject.CreationException;

public abstract class ConfigUtil {

  public static final String PROPERTY_ANLAGE = "v5t11.anlage";
  public static final String PROPERTY_CONFIG_DIR = "v5t11.configDir";
  public static final String PROPERTY_IF_TYP = "v5t11.ifTyp";
  public static final String PROPERTY_PORT_NAME = "v5t11.portName";
  public static final String PROPERTY_PORT_SPEED = "v5t11.portSpeed";

  public static final String DEFAULT_ANLAGE = "show";
  public static final String DEFAULT_IF_TYP = "rautenhaus";
  public static final String DEFAULT_PORT_NAME = "auto";

  private static final Properties v5t11Properties = new Properties();

  static {
    try (Reader reader = new FileReader(getConfigDir() + "/v5t11.properties")) {
      v5t11Properties.load(reader);
    } catch (Exception e) {
      // ignore
    }
  }

  public static String getProperty(String name, String defaultValue) {
    String value = System.getenv(name);
    if (value == null) {
      value = System.getProperty(name);
    }
    if (value == null) {
      value = v5t11Properties.getProperty(name);
    }
    if (value == null) {
      value = defaultValue;
    }
    return value;
  }

  public static String getConfigDir() {
    String configDir = getProperty(PROPERTY_CONFIG_DIR, null);
    if (configDir == null) {
      configDir = System.getProperty("user.home") + "/v5t11";
    }
    return configDir;
  }

  public static String getPortName() {
    return getProperty(PROPERTY_PORT_NAME, DEFAULT_PORT_NAME);
  }

  public static int getPortSpeed() {
    return Integer.parseInt(getProperty(PROPERTY_PORT_SPEED, "0"));
  }

  public static String getAnlage() {
    return getProperty(PROPERTY_ANLAGE, DEFAULT_ANLAGE);
  }

  public static <T> T readXmlConfig(String fileNameSuffix, Class<T> clazz) {
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

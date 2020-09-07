package de.gedoplan.v5t11.util.config;

import java.io.FileReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.microprofile.config.spi.ConfigSource;

public class V5t11ConfigSource implements ConfigSource {

  private Map<String, String> v5t11Properties = new HashMap<>();

  public V5t11ConfigSource() {
    String configDir = System.getProperty(ConfigBase.PROPERTY_CONFIG_DIR);
    if (configDir == null) {
      configDir = System.getenv(ConfigBase.PROPERTY_CONFIG_DIR);
    }
    if (configDir == null) {
      configDir = System.getProperty("user.home") + "/v5t11";
    }

    Properties prop = new Properties();
    try (Reader reader = new FileReader(configDir + "/v5t11.properties")) {
      prop.load(reader);
    } catch (Exception e) {
      // ignore
    }

    prop.forEach((k, v) -> this.v5t11Properties.put(k.toString(), v.toString()));
    this.v5t11Properties.put(ConfigBase.PROPERTY_CONFIG_DIR, configDir);
  }

  @Override
  public int getOrdinal() {
    return 200;
  }

  @Override
  public Map<String, String> getProperties() {
    return this.v5t11Properties;
  }

  @Override
  public String getValue(String propertyName) {
    return this.v5t11Properties.get(propertyName);
  }

  @Override
  public String getName() {
    return this.getClass().getSimpleName();
  }

}

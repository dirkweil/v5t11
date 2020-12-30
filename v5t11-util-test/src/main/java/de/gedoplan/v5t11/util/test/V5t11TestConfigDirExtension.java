package de.gedoplan.v5t11.util.test;

import de.gedoplan.baselibs.utils.util.ResourceUtil;
import de.gedoplan.v5t11.util.config.ConfigBase;

import java.net.URL;
import java.nio.file.Paths;

import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class V5t11TestConfigDirExtension implements BeforeAllCallback {

  public static final String TEST_CONFIG_FILE = "testConfig/v5t11.properties";

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    URL url = ResourceUtil.getResource(TEST_CONFIG_FILE);
    if (url == null) {
      throw new AssertionError("Missing test config directory; please create the file " + TEST_CONFIG_FILE + " in the current project");
    }
    String configDir = Paths.get(url.toURI()).getParent().toString();
    System.setProperty(ConfigBase.PROPERTY_CONFIG_DIR, configDir);
  }

  // public V5t11TestConfigDirExtension() {
  // System.out.println("Gonzo was here");
  // }

}

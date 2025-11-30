package de.gedoplan.v5t11.leitstand.testenvironment.profile;

import de.gedoplan.baselibs.utils.util.ResourceUtil;
import de.gedoplan.v5t11.util.config.ConfigBase;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import io.quarkus.test.junit.QuarkusTestProfile;

/**
 * Profil für Tests.
 * Das Profil hat diese Eigenschaften:
 * <ul>
 * <li>Als Konfigurationsverzeichnis den Resource-Ordner testConfig nutzen.</li>
 * <li>Als DB H2 nutzen.</li>
 * <li>Kafka Dev Service abschalten.</li>
 * <li>Messaging-Kanäle auf InMemoryConnector umstellen.</li>
 * </ul>
 */
public class V5T11Test implements QuarkusTestProfile {

  public static final String TEST_CONFIG_FILE = "testConfig/v5t11.properties";

  @Override
  public Map<String, String> getConfigOverrides() {
    return Map.of(
      ConfigBase.PROPERTY_CONFIG_DIR, getTestConfigDirName(),
      "quarkus.datasource.db-kind", "h2",
      "quarkus.kafka.devservices.enabled", "false");
  }

  @Override
  public List<TestResourceEntry> testResources() {
    return Collections.singletonList(new TestResourceEntry(InMemoryConnectorLifecycleManager.class));
  }

  private static String getTestConfigDirName() {
    URL url = ResourceUtil.getResource(TEST_CONFIG_FILE);
    if (url == null) {
      throw new AssertionError("Missing test config directory; please create the file " + TEST_CONFIG_FILE + " in the current project");
    }
    try {
      return Paths.get(url.toURI()).getParent().toString();
    } catch (URISyntaxException e) {
      throw new RuntimeException("Malformed resource file url", e);
    }
  }
}

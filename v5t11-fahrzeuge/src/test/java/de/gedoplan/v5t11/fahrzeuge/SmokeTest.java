package de.gedoplan.v5t11.fahrzeuge;

import de.gedoplan.v5t11.fahrzeuge.testenvironment.profile.V5T11Test;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestProfile(V5T11Test.class)
public class SmokeTest {

  @Inject
  Logger logger;

  @Test
  void test() {
    this.logger.info("Quarkus running ...");
  }
}

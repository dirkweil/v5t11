package de.gedoplan.v5t11.fahrzeuge;

import io.quarkus.test.junit.QuarkusTest;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

@QuarkusTest
public class SmokeTest {

  @Inject
  Logger logger;

  @Test
  void test() {
    this.logger.info("Quarkus running ...");
  }
}

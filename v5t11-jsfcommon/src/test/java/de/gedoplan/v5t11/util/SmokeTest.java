package de.gedoplan.v5t11.util;


import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

import io.quarkus.test.junit.QuarkusTest;

@QuarkusTest
public class SmokeTest {
    
    @Inject
    Logger logger;

    @Test
    void test() {
      logger.info("Quarkus running ...");
    }
}
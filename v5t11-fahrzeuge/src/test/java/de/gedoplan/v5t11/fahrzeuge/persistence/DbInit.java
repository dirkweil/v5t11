package de.gedoplan.v5t11.fahrzeuge.persistence;

import de.gedoplan.v5t11.fahrzeuge.TestBase;
import de.gedoplan.v5t11.fahrzeuge.testenvironment.profile.V5T11Test;

import jakarta.inject.Inject;

import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.junit.TestProfile;
import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;

/**
 * DB mit Testdaten füllen.
 * <p>
 * Das eigentliche Füllen der DB geschieht in {@link TestBase#fillDb()}.
 * <p>
 * Soll statt der Test-DB (in-memory) die Produktions-DB genutzt werden, muss deren URL als Property im
 * Aufruf angegeben werden: -Dquarkus.datasource.url=jdbc:h2:~/h2/v5t11;AUTO_SERVER=TRUE
 *
 * @author dw
 */
@QuarkusTest
@TestProfile(V5T11Test.class)
public class DbInit extends TestBase {

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Inject
  Logger log;

  @Test
  public void showFahrzeuge() {
    this.log.info("Fahrzeuge:");
    this.fahrzeugRepository
      .findAll()
      .forEach(f -> this.log.info("  " + f));
  }

}

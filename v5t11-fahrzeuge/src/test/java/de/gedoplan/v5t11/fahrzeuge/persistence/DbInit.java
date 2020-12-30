package de.gedoplan.v5t11.fahrzeuge.persistence;

import de.gedoplan.v5t11.fahrzeuge.TestBase;
import de.gedoplan.v5t11.util.test.V5t11TestConfigDirExtension;

import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.extension.ExtendWith;

import io.quarkus.test.junit.QuarkusTestExtension;

@ExtendWith({ V5t11TestConfigDirExtension.class, QuarkusTestExtension.class })
@TestMethodOrder(MethodOrderer.Alphanumeric.class)
/**
 * DB mit Testdaten füllen.
 * 
 * Das eigentliche Füllen der DB geschieht in {@link TestBase#dbInit}.
 * 
 * Soll statt der Test-DB (in-memory) die Produktions-DB genutzt werden, muss deren URL als Property im
 * Aufruf angegeben werden: -Dquarkus.datasource.url=jdbc:h2:~/h2/v5t11;AUTO_SERVER=TRUE
 * 
 * @author dw
 *
 */
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

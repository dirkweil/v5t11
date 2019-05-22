package de.gedoplan.v5t11.fahrstrassen;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;

import java.util.concurrent.Semaphore;

import javax.enterprise.inject.spi.CDI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.meecrowave.Meecrowave;

public class FahrstrassenMain {

  @SuppressWarnings("resource")
  public static void main(String[] args) {

    // JUL in Log4j2 leiten; Achtung: Property muss vor jeglicher Log-Aktivität gesetzt werden!
    System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");

    Log log = LogFactory.getLog(FahrstrassenMain.class);

    log.info("Fahrstrassen-Service wird gestartet");

    try (Meecrowave meecrowave = new Meecrowave().bake()) {
      Parcours parcours = CDI.current().select(Parcours.class).get();
      log.info("Fahrstrassen-Service gestartet für Bereiche " + parcours.getBereiche());

      new Semaphore(-1).acquire();
    } catch (Exception e) {

      // TODO Meecrowave scheint die Logger beim Shutdown zu schliessen
      if (log.isErrorEnabled()) {
        log.error("Kann Fahrstrassen-Service nicht starten", e);
      } else {
        System.err.println("Kann Fahrstrassen-Service nicht starten: " + e);
      }
    }

    System.exit(0);
  }

}

package de.gedoplan.v5t11.leitstand;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;

import java.util.Scanner;

import javax.enterprise.inject.spi.CDI;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.meecrowave.Meecrowave;

public class LeitstandMain {

  @SuppressWarnings("resource")
  public static void main(String[] args) {

    // JUL in Log4j2 leiten; Achtung: Property muss vor jeglicher Log-Aktivität gesetzt werden!
    System.setProperty("java.util.logging.manager", "org.apache.logging.log4j.jul.LogManager");

    Log log = LogFactory.getLog(LeitstandMain.class);

    try (Meecrowave meecrowave = new Meecrowave().bake()) {
      Leitstand leitstand = CDI.current().select(Leitstand.class).get();
      log.info("Fahrstrassen-Service gestartet für Bereiche " + leitstand);

      // TODO Wait for \n in stdin before terminating
      new Scanner(System.in).nextLine();

    } catch (Exception e) {

      // TODO Meecrowave scheint die Logger beim Shutdown zu schliessen
      if (log.isErrorEnabled()) {
        log.error("Kann Leitstand-Service nicht starten", e);
      } else {
        System.err.println("Kann Leitstand-Service nicht starten: " + e);
      }
    }

    System.exit(0);
  }

}

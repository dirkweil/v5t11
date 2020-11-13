package de.gedoplan.v5t11.status.service.init;

import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.status.persistence.FahrzeugRepository;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.apache.commons.logging.Log;

import io.quarkus.runtime.StartupEvent;

/**
 * Initialisierungsservice für Demo-Daten.
 *
 * Dieser Service nutzt den Lifecycle-Event nach der Erzeugung des Application Scopes zur Ausführung einer Methode, die einige
 * Loks in die Datenbank schreibt, wenn die Zieltabelle noch komplett leer ist.
 *
 * @author dw
 */
@ApplicationScoped
public class InitLokDataService {

  @Inject
  FahrzeugRepository lokRepository;

  @Inject
  Log log;

  /**
   * Create test/demo data.
   * Attn: Interceptors may not be called, when method is private!
   *
   * @param event
   *          Application scope initialization event
   */
  @Transactional
  // protected void createDemoData(@Observes @Initialized(ApplicationScoped.class) Object event) {
  protected void createDemoData(@Observes StartupEvent event) {
    try {
      this.log.debug("Lok-Daten erzeugen");

      // TODO Entfernen, wenn Pflegedialog existiert
      this.lokRepository.findAll().forEach(this.lokRepository::remove);

      if (this.lokRepository.countAll() == 0) {
        for (Fahrzeug lok : TestLokData.loks) {
          this.lokRepository.persist(lok);
        }
      }
    } catch (Exception e) {
      this.log.warn("Kann keine Lok-Daten erzeugen", e);
    }

  }

}

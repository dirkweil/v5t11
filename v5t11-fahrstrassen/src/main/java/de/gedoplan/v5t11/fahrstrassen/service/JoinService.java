package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.messaging.OutgoingHandler;
import de.gedoplan.v5t11.fahrstrassen.persistence.GleisRepository;
import de.gedoplan.v5t11.fahrstrassen.persistence.SignalRepository;
import de.gedoplan.v5t11.fahrstrassen.persistence.WeicheRepository;
import de.gedoplan.v5t11.util.cdi.Received;
import de.gedoplan.v5t11.util.domain.JoinInfo;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import org.jboss.logging.Logger;

/**
 * Service für den Beitritt einer Anwendung zum Gesamtsystem.
 * 
 * Wird aufgerufen, wenn diese Anwendung startet oder sich eine andere (durch deren JoinService) per Messaging meldet.
 * 
 * @author dw
 *
 */
@ApplicationScoped
public class JoinService {

  @Inject
  OutgoingHandler outgoingHandler;

  @Inject
  Parcours parcours;

  @Inject
  ConfigService configService;

  @Inject
  GleisRepository gleisRepository;

  @Inject
  SignalRepository signalRepository;

  @Inject
  WeicheRepository weicheRepository;

  @Inject
  Logger logger;

  /**
   * Andere Teilanwendungen vom eigenen Anwendungsstart informieren.
   */
  public void joinMyself() {
    // Letzte Änderung der persistenten Daten berechnen
    long lastChangeMillis = Long.max(Long.max(
        this.gleisRepository.findMaxLastChangeMillis(),
        this.signalRepository.findMaxLastChangeMillis()),
        this.weicheRepository.findMaxLastChangeMillis());

    // Vorsichtshalber ein paar Sekunden abziehen
    lastChangeMillis -= 10 * 1000L;

    // Aktualisierungen von anderen Anwendungen anfordern
    this.logger.debugf("Updates ab %tF %<tT.%<tL anfordern", lastChangeMillis);
    this.outgoingHandler.publish(new JoinInfo(this.configService.getArtifactId(), lastChangeMillis));

    // Eigenen Status an die anderen senden
    join(0);
  }

  /**
   * Eigenen Status an eine andere, gerade gestartete Teilanwendung senden.
   * 
   * @param joinInfo Info über die andere Anwendung
   */
  void appJoined(@ObservesAsync @Received JoinInfo joinInfo) {
    if (!joinInfo.getAppName().equals(this.configService.getArtifactId())) {
      this.logger.debugf("%s fordert Updates ab %tF %<tT.%<tL an", joinInfo.getAppName(), joinInfo.getLastUpdateMillis());
      join(joinInfo.getLastUpdateMillis());
    }
  }

  private void join(long sendUpdatesSinceMillis) {
    this.logger.debugf("Updates ab %tF %<tT.%<tL senden", sendUpdatesSinceMillis);
    this.parcours
        .getFahrstrassen()
        .stream()
        .forEach(x -> this.outgoingHandler.publish(x));
    this.logger.debugf("%d Fahrstrassen gesendet", this.parcours.getFahrstrassen().size());
  }

}

package de.gedoplan.v5t11.fahrzeuge.service;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.gateway.StatusGateway;
import de.gedoplan.v5t11.fahrzeuge.messaging.OutgoingHandler;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import de.gedoplan.v5t11.util.cdi.Received;
import de.gedoplan.v5t11.util.domain.JoinInfo;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

@ApplicationScoped
public class JoinService {

  @Inject
  OutgoingHandler outgoingHandler;

  @Inject
  ConfigService configService;

  @Inject
  @RestClient
  StatusGateway statusGateway;

  @Inject
  FahrzeugRepository fahrzeugRepository;

  @Inject
  Logger logger;

  /**
   * Andere Teilanwendungen vom eigenen Anwendungsstart informieren.
   */
  public void joinMyself() {
    // Letzte Änderung der persistenten Daten berechnen
    // TODO lastChangeMillis berechnen
    long lastChangeMillis = 0L;

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
    List<Fahrzeug> fahrzeuge = this.fahrzeugRepository.findAll();
    fahrzeuge.forEach(this.outgoingHandler::publish);
    this.logger.debugf("%d Fahrzeuge gesendet", fahrzeuge.size());
  }

}

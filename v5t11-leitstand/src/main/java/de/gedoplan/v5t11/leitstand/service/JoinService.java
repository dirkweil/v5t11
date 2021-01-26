package de.gedoplan.v5t11.leitstand.service;

import de.gedoplan.v5t11.leitstand.messaging.OutgoingHandler;
import de.gedoplan.v5t11.util.cdi.Received;
import de.gedoplan.v5t11.util.domain.JoinInfo;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import org.jboss.logging.Logger;

@ApplicationScoped
public class JoinService {

  @Inject
  OutgoingHandler outgoingHandler;

  @Inject
  ConfigService configService;

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
   * @param joinInfo
   *        Info über die andere Anwendung
   */
  void appJoined(@ObservesAsync @Received JoinInfo joinInfo) {
    if (!joinInfo.getAppName().equals(this.configService.getArtifactId())) {
      this.logger.debugf("%s fordert Updates ab %tF %<tT.%<tL an", joinInfo.getAppName(), joinInfo.getLastUpdateMillis());
      join(joinInfo.getLastUpdateMillis());
    }
  }

  private void join(long sendUpdatesSinceMillis) {
    this.logger.debugf("Updates ab %tF %<tT.%<tL senden (nichts zu tun)", sendUpdatesSinceMillis);
  }
}

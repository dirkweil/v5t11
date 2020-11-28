package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.fahrstrassen.messaging.OutgoingHandler;
import de.gedoplan.v5t11.util.cdi.Received;
import de.gedoplan.v5t11.util.domain.JoinInfo;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

@ApplicationScoped
public class JoinService {

  @Inject
  OutgoingHandler outgoingHandler;

  @Inject
  Parcours steuerung;

  @Inject
  ConfigService configService;

  /**
   * Andere Teilanwendungen vom eigenen Anwendungsstart informieren.
   */
  public void joinMyself() {
    // Aktualisierungen von anderen Anwendungen anfordern
    // TODO lastUpdateMillis berechnen
    long lastUpdateMillis = 0L;
    this.outgoingHandler.publish(new JoinInfo(this.configService.getArtifactId(), lastUpdateMillis));

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
      join(joinInfo.getLastUpdateMillis());
    }
  }

  private void join(long sendUpdatesSinceMillis) {
    // this.steuerung
    // .getFahrstrassen()
    // .stream()
    // .forEach(x -> this.outgoingHandler.publish(x));
  }

}

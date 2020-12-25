package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.messaging.OutgoingHandler;
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
  Steuerung steuerung;

  @Inject
  ConfigService configService;

  /**
   * Andere Teilanwendungen vom eigenen Anwendungsstart informieren.
   */
  public void joinMyself() {
    // Aktualisierungen von anderen Anwendungen anfordern
    this.outgoingHandler.publish(new JoinInfo(this.configService.getArtifactId(), 0L));

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
    this.outgoingHandler.publish(this.steuerung.getZentrale());

    this.steuerung
        .getGleisabschnitte()
        .stream()
        .filter(x -> x.getLastChangeMillis() >= sendUpdatesSinceMillis)
        .forEach(x -> this.outgoingHandler.publish(x));

    this.steuerung
        .getSignale()
        .stream()
        .filter(x -> x.getLastChangeMillis() >= sendUpdatesSinceMillis)
        .forEach(x -> this.outgoingHandler.publish(x));

    this.steuerung
        .getWeichen()
        .stream()
        .filter(x -> x.getLastChangeMillis() >= sendUpdatesSinceMillis)
        .forEach(x -> this.outgoingHandler.publish(x));

    this.steuerung
        .getFahrzeuge()
        .stream()
        .filter(x -> x.getLastChangeMillis() >= sendUpdatesSinceMillis)
        .forEach(x -> this.outgoingHandler.publish(x));
  }

}

package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.messaging.OutgoingHandler;
import de.gedoplan.v5t11.util.cdi.Received;
import de.gedoplan.v5t11.util.domain.JoinInfo;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.inject.Inject;

import org.jboss.logging.Logger;

@ApplicationScoped
public class JoinService {

  @Inject
  OutgoingHandler outgoingHandler;

  @Inject
  Steuerung steuerung;

  @Inject
  ConfigService configService;

  @Inject
  Logger logger;

  @PostConstruct
  void postConstruct() {
    logger.debugf("Initialized JoinService");
  }

  /**
   * Andere Teilanwendungen vom eigenen Anwendungsstart informieren.
   */
  public void joinMyself() {
    join(0);
  }

  /**
   * Eigenen Status an eine andere, gerade gestartete Teilanwendung senden.
   * 
   * @param joinInfo Info über die andere Anwendung
   */
// TODO Das wird vermutlich nicht mehr gebraucht, da die anderen MS die ggf. verpassten Meldungen von Kafka nachgeliefert bekommen
  void appJoined(@ObservesAsync @Received JoinInfo joinInfo) {
    if (!joinInfo.getAppName().equals(this.configService.getArtifactId())) {
      this.logger.debugf("%s fordert Updates ab %tF %<tT.%<tL an", joinInfo.getAppName(), joinInfo.getLastUpdateMillis());
      // join(joinInfo.getLastUpdateMillis());
    }
  }

  private void join(long sendUpdatesSinceMillis) {
    this.logger.debugf("Updates ab %tF %<tT.%<tL senden", sendUpdatesSinceMillis);

    this.outgoingHandler.publish(this.steuerung.getZentrale());
    this.logger.debugf("Zentrale gesendet");

    long count = this.steuerung
        .getGleise()
        .stream()
        .filter(x -> x.getLastChangeMillis() >= sendUpdatesSinceMillis)
        .peek(x -> this.outgoingHandler.publish(x))
        .count();
    this.logger.debugf("%d Gleise gesendet", count);

    count = this.steuerung
        .getSignale()
        .stream()
        .filter(x -> x.getLastChangeMillis() >= sendUpdatesSinceMillis)
        .peek(x -> this.outgoingHandler.publish(x))
        .count();
    this.logger.debugf("%d Signale gesendet", count);

    count = this.steuerung
        .getWeichen()
        .stream()
        .filter(x -> x.getLastChangeMillis() >= sendUpdatesSinceMillis)
        .peek(x -> this.outgoingHandler.publish(x))
        .count();
    this.logger.debugf("%d Weichen gesendet", count);

    count = this.steuerung
        .getFahrzeuge()
        .stream()
        .filter(x -> x.getLastChangeMillis() >= sendUpdatesSinceMillis)
        .peek(x -> this.outgoingHandler.publish(x))
        .count();
    this.logger.debugf("%d Fahrzeuge gesendet", count);
  }

}

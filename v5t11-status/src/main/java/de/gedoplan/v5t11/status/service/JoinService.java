package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.status.messaging.OutgoingHandler;
import de.gedoplan.v5t11.util.cdi.Received;

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

  /**
   * Andere Teilanwendungen vom eigenen Anwendungsstart informieren.
   */
  public void joinMyself() {
    join(0);
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

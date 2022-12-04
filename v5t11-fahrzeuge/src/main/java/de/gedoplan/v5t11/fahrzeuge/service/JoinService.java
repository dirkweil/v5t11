package de.gedoplan.v5t11.fahrzeuge.service;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.fahrzeuge.gateway.StatusGateway;
import de.gedoplan.v5t11.fahrzeuge.messaging.OutgoingHandler;
import de.gedoplan.v5t11.fahrzeuge.persistence.FahrzeugRepository;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.List;

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
    join(0);
  }

  private void join(long sendUpdatesSinceMillis) {
    this.logger.debugf("Updates ab %tF %<tT.%<tL senden", sendUpdatesSinceMillis);
    List<Fahrzeug> fahrzeuge = this.fahrzeugRepository.findAll();
    fahrzeuge.forEach(this.outgoingHandler::publish);
    this.logger.debugf("%d Fahrzeuge gesendet", fahrzeuge.size());
  }

}

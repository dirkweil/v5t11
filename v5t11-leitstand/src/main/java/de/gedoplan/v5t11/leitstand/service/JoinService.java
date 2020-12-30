package de.gedoplan.v5t11.leitstand.service;

import de.gedoplan.v5t11.leitstand.messaging.OutgoingHandler;
import de.gedoplan.v5t11.util.domain.JoinInfo;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class JoinService {

  @Inject
  OutgoingHandler outgoingHandler;

  @Inject
  ConfigService configService;

  /**
   * Andere Teilanwendungen vom eigenen Anwendungsstart informieren.
   */
  public void joinMyself() {
    // Aktualisierungen von anderen Anwendungen anfordern
    long lastUpdateMillis = 0L;
    this.outgoingHandler.publish(new JoinInfo(this.configService.getArtifactId(), lastUpdateMillis));
  }
}

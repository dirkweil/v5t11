package de.gedoplan.v5t11.leitstand.service;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;

@ApplicationScoped
public class JoinService {

  @Inject
  Logger logger;

  /**
   * Andere Teilanwendungen vom eigenen Anwendungsstart informieren.
   */
  public void joinMyself() {
    join(0);
  }

  private void join(long sendUpdatesSinceMillis) {
    // this.logger.debugf("Updates ab %tF %<tT.%<tL senden (nichts zu tun)", sendUpdatesSinceMillis);
  }
}

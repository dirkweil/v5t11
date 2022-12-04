package de.gedoplan.v5t11.fahrzeuge.service;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import org.jboss.logging.Logger;

import de.gedoplan.v5t11.util.jsf.NavigationPresenter;
import io.quarkus.runtime.StartupEvent;

@Dependent
public class BootStrap {

  void boot(@Observes StartupEvent startupEvent,
      Logger log,
      ConfigService configService,
      DbInitService dbInitService,
      JoinService joinService,
      NavigationPresenter navigationPresenter) {
    log.infof("app: %s:%s", configService.getArtifactId(), configService.getVersion());

    log.infof("configDir: %s", configService.getConfigDir());
    log.infof("anlage: %s", configService.getAnlage());
    log.infof("db: %s:%d", configService.getDbHost(), configService.getDbPort());
    log.infof("mqttBroker: %s:%d", configService.getMqttHost(), configService.getMqttPort());
    log.infof("statusRestUrl: %s", configService.getStatusRestUrl());

    dbInitService.fillDb();

    joinService.joinMyself();
  }

}

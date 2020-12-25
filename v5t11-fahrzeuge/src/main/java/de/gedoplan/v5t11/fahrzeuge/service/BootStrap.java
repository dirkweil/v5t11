package de.gedoplan.v5t11.fahrzeuge.service;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import org.jboss.logging.Logger;

import io.quarkus.runtime.StartupEvent;

@Dependent
public class BootStrap {

  void boot(@Observes StartupEvent startupEvent,
      Logger log,
      JoinService joinService,
      ConfigService configService) {
    log.info("app: " + configService.getArtifactId() + ":" + configService.getVersion());

    log.info("configDir: " + configService.getConfigDir());
    log.info("anlage: " + configService.getAnlage());
    log.info("mqttBroker: " + configService.getMqttHost() + ":" + configService.getMqttPort());
    log.info("statusRestUrl: " + configService.getStatusRestUrl());

    joinService.joinMyself();
  }

}

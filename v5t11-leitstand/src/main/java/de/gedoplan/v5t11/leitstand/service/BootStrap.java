package de.gedoplan.v5t11.leitstand.service;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.stellwerk.StellwerkUIStarter;

import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import org.jboss.logging.Logger;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.StartupEvent;

@Dependent
public class BootStrap {

  void boot(@Observes StartupEvent startupEvent,
      Logger log,
      ConfigService configService,
      Leitstand leitstand,
      StellwerkUIStarter stellwerkUIStarter) {
    log.infof("app: %s:%s", configService.getArtifactId(), configService.getVersion());

    log.infof("configDir: %s", configService.getConfigDir());
    log.infof("anlage: %s", configService.getAnlage());
    log.infof("db: %s:%d", configService.getDbHost(), configService.getDbPort());
    log.infof("mqttBroker: %s:%d", configService.getMqttHost(), configService.getMqttPort());
    log.infof("statusRestUrl: %s", configService.getStatusRestUrl());
    log.infof("fahrstrassenRestUrl: %s", configService.getFahrstrassenRestUrl());
    log.infof("bereiche: %s", leitstand.getBereiche().stream().collect(Collectors.joining(",")));

    try {
      stellwerkUIStarter.start();
    } catch (Exception e) {
      log.error("Kann UI nicht starten", e);

      Quarkus.asyncExit(1);
    }
  }

}

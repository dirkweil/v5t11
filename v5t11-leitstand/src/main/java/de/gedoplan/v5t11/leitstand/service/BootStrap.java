package de.gedoplan.v5t11.leitstand.service;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import org.jboss.logging.Logger;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@Dependent
public class BootStrap {

  private final static ExecutorService scheduler = Executors.newSingleThreadExecutor();

  void boot(@Observes StartupEvent startupEvent,
      Logger log,
      ConfigService configService,
      JoinService joinService,
      Leitstand leitstand) {
    log.info("app: " + configService.getArtifactId() + ":" + configService.getVersion());

    log.info("configDir: " + configService.getConfigDir());
    log.info("anlage: " + configService.getAnlage());
    log.info("mqttBroker: " + configService.getMqttHost() + ":" + configService.getMqttPort());
    log.info("statusRestUrl: " + configService.getStatusRestUrl());
    log.info("fahrstrassenRestUrl: " + configService.getFahrstrassenRestUrl());
    log.info("bereiche: " + leitstand.getBereiche().stream().collect(Collectors.joining(",")));

    joinService.joinMyself();

    // try {
    // StellwerkUI.start();
    // } catch (Exception e) {
    // log.error("Kann UI nicht starten", e);
    //
    // Quarkus.asyncExit(1);
    // }
  }

  void terminate(@Observes ShutdownEvent shutdownEvent) {
    scheduler.shutdown();
  }
}

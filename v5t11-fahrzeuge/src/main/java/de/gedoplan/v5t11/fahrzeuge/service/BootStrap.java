package de.gedoplan.v5t11.fahrzeuge.service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
      StatusUpdater statusUpdater) {
    log.info("app: " + configService.getArtifactId() + ":" + configService.getVersion());

    log.info("configDir: " + configService.getConfigDir());
    log.info("anlage: " + configService.getAnlage());
    log.info("statusRestUrl: " + configService.getStatusRestUrl());
    log.info("statusJmsUrl: " + configService.getStatusJmsUrl());

    // TODO JMS -> RM
    // scheduler.submit(statusUpdater);
  }

  void terminate(@Observes ShutdownEvent shutdownEvent) {
    scheduler.shutdown();
  }
}

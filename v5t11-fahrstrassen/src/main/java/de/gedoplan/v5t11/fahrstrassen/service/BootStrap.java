package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import org.apache.commons.logging.Log;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@Dependent
public class BootStrap {

  private final static ExecutorService scheduler = Executors.newSingleThreadExecutor();

  void boot(@Observes StartupEvent startupEvent,
      Log log,
      ConfigService configService,
      Parcours parcours,
      StatusUpdater statusUpdater) {
    log.info("app: " + configService.getArtifactId() + ":" + configService.getVersion());

    log.info("configDir: " + configService.getConfigDir());
    log.info("anlage: " + configService.getAnlage());
    log.info("statusRestUrl: " + configService.getStatusRestUrl());
    log.info("statusJmsUrl: " + configService.getStatusJmsUrl());

    log.info("#fahrstrassen: " + parcours.getFahrstrassen().size());

    scheduler.submit(statusUpdater);
  }

  void terminate(@Observes ShutdownEvent shutdownEvent) {
    scheduler.shutdown();
  }
}

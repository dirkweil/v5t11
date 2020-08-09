package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;

import org.apache.commons.logging.Log;

import io.quarkus.runtime.StartupEvent;

@Dependent
public class BootStrap {

  void boot(@Observes StartupEvent startupEvent,
      Log log,
      ConfigService configService,
      Parcours parcours,
      ParcoursStatusUpdater parcoursStatusUpdater) {
    log.info("app: " + configService.getArtifactId() + ":" + configService.getVersion());

    log.info("configDir: " + configService.getConfigDir());
    log.info("anlage: " + configService.getAnlage());
    log.info("statusRestUrl: " + configService.getStatusRestUrl());
    log.info("statusJmsUrl: " + configService.getStatusJmsUrl());

    log.info("#fahrstrassen: " + parcours.getFahrstrassen().size());

    parcoursStatusUpdater.run(parcours);
  }
}

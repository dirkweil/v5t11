package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.util.cdi.Created;
import de.gedoplan.v5t11.util.cdi.EventFirer;

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
      JoinService joinService,
      Parcours parcours,
      StatusUpdater statusUpdater,
      EventFirer eventFirer) {
    try {
      log.info("app: " + configService.getArtifactId() + ":" + configService.getVersion());

      log.info("configDir: " + configService.getConfigDir());
      log.info("anlage: " + configService.getAnlage());
      log.info("mqttBroker: " + configService.getMqttHost() + ":" + configService.getMqttPort());
      log.info("statusRestUrl: " + configService.getStatusRestUrl());

      log.info("#fahrstrassen: " + parcours.getFahrstrassen().size());
      log.info("#autoFahrstrassen: " + parcours.getAutoFahrstrassen().size());

      eventFirer.fire(parcours, Created.Literal.INSTANCE);

      joinService.joinMyself();
    } catch (Exception e) {
      log.error("Kann Anwendung nicht starten", e);
      Quarkus.asyncExit(1);
    }
  }
}

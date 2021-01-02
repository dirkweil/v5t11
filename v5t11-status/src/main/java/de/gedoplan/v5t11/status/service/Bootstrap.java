package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Steuerung;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.interceptor.Interceptor;

import org.jboss.logging.Logger;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@Dependent
public class Bootstrap {

  private final static ExecutorService scheduler = Executors.newSingleThreadExecutor();

  void boot(@Observes @Priority(Interceptor.Priority.APPLICATION + 999) StartupEvent startupEvent,
      ConfigService configService,
      JoinService joinService,
      Steuerung steuerung,
      Logger log) {
    log.infof("app: %s:%s", configService.getArtifactId(), configService.getVersion());

    log.infof("configDir: %s", configService.getConfigDir());
    log.infof("anlage: %s", configService.getAnlage());
    log.infof("db: %s:%d", configService.getDbHost(), configService.getDbPort());
    log.infof("mqttBroker: %s:%d", configService.getMqttHost(), configService.getMqttPort());

    joinService.joinMyself();

    steuerung.open(scheduler);
  }

  void terminate(@Observes ShutdownEvent shutdownEvent, Steuerung steuerung) {
    steuerung.close();
    scheduler.shutdown();
  }

}

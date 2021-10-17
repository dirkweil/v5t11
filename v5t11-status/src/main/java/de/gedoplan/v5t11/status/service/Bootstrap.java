package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.util.jsf.NavigationPresenter;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Priority;
import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.interceptor.Interceptor;

import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.config.inject.ConfigProperty;
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
      AnlagenstatusService anlagenstatusService,
      Logger log,
      NavigationPresenter navigationPresenter) {
    log.infof("app: %s:%s", configService.getArtifactId(), configService.getVersion());

    log.infof("configDir: %s", configService.getConfigDir());
    log.infof("anlage: %s", configService.getAnlage());
    log.infof("db: %s:%d", configService.getDbHost(), configService.getDbPort());
    log.infof("mqttBroker: %s:%d", configService.getMqttHost(), configService.getMqttPort());
    log.infof("statusWebUrl: %s", configService.getStatusWebUrl());

    joinService.joinMyself();

    steuerung.open(scheduler);

    anlagenstatusService.init();

    // TODO: Workaround für Startup
    navigationPresenter.toString();
  }

  void terminate(@Observes ShutdownEvent shutdownEvent, Steuerung steuerung) {
    // Aktualisierungen stoppen
    scheduler.shutdown();

    // Verbindung zur Zentrale schließen
    steuerung.close();
  }

}

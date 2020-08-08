package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Steuerung;

import java.util.concurrent.Executors;

import javax.annotation.Resource;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

import org.apache.commons.logging.Log;

@Dependent
public class Bootstrap {

  @Resource
  ManagedExecutorService executorService;

  void boot(@Observes @Initialized(ApplicationScoped.class) Object object, ConfigService configService, Steuerung steuerung, Log log) {
    log.info("app: " + configService.getArtifactId() + ":" + configService.getVersion());

    log.info("configDir: " + configService.getConfigDir());
    log.info("anlage: " + configService.getAnlage());

    steuerung.open(this.executorService != null ? this.executorService : Executors.newSingleThreadExecutor());
  }

  void shutdown(@Observes @Destroyed(ApplicationScoped.class) Object object, Steuerung steuerung) {
    steuerung.close();
  }
}

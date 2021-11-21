package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.util.jsf.NavigationPresenter;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.sql.DataSource;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.quarkus.runtime.ShutdownEvent;
import io.quarkus.runtime.StartupEvent;

@Dependent
public class Bootstrap {

  private final static ExecutorService scheduler = Executors.newSingleThreadExecutor();

  void boot(@Observes StartupEvent startupEvent,
      ConfigService configService,
      DataSource dataSource,
      @ConfigProperty(name = "kafka.bootstrap.servers", defaultValue = "(dev service; see url above)") String kafkaUrl,
      JoinService joinService,
      Steuerung steuerung,
      AnlagenstatusService anlagenstatusService,
      Logger log,
      NavigationPresenter navigationPresenter) {
    log.infof("app: %s:%s", configService.getArtifactId(), configService.getVersion());

    log.infof("configDir: %s", configService.getConfigDir());
    log.infof("anlage: %s", configService.getAnlage());
    log.infof("db: %s", getDbUrl(dataSource));
    log.infof("kafka: %s", kafkaUrl);
    log.infof("statusWebUrl: %s", configService.getStatusWebUrl());

    joinService.joinMyself();

    steuerung.open(scheduler);

    anlagenstatusService.init();
  }

  private String getDbUrl(DataSource dataSource) {
    try (Connection connection = dataSource.getConnection()) {
      return connection.getMetaData().getURL();
    } catch (SQLException e) {
      return e.toString();
    }
  }

  void terminate(@Observes ShutdownEvent shutdownEvent, Steuerung steuerung) {
    // Aktualisierungen stoppen
    scheduler.shutdown();

    // Verbindung zur Zentrale schließen
    steuerung.close();
  }

}

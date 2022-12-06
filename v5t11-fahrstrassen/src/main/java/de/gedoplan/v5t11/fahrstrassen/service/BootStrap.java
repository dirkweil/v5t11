package de.gedoplan.v5t11.fahrstrassen.service;

import de.gedoplan.v5t11.fahrstrassen.entity.Parcours;
import de.gedoplan.v5t11.util.cdi.Created;
import de.gedoplan.v5t11.util.cdi.EventFirer;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.sql.DataSource;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.StartupEvent;

import java.sql.Connection;
import java.sql.SQLException;

@Dependent
public class BootStrap {

  void boot(@Observes StartupEvent startupEvent,
    Logger log,
    ConfigService configService,
    DataSource dataSource,
    @ConfigProperty(name = "kafka.bootstrap.servers", defaultValue = "(dev service; see url above)") String kafkaUrl,
    Parcours parcours,
    JoinService joinService,
    EventFirer eventFirer) {
    try {
      log.infof("app: %s:%s", configService.getArtifactId(), configService.getVersion());

      log.infof("configDir: %s", configService.getConfigDir());
      log.infof("anlage: %s", configService.getAnlage());
      log.infof("db: %s", getDbUrl(dataSource));
      log.infof("kafka: %s", kafkaUrl);
      log.infof("statusRestUrl: %s", configService.getStatusRestUrl());

      log.infof("#fahrstrassen: %d", parcours.getFahrstrassen().size());
      log.infof("#autoFahrstrassen: %d", parcours.getAutoFahrstrassen().size());

      eventFirer.fire(parcours, Created.Literal.INSTANCE);

      joinService.joinMyself();
    } catch (Exception e) {
      log.error("Kann Anwendung nicht starten", e);
      Quarkus.asyncExit(1);
    }
  }

  private String getDbUrl(DataSource dataSource) {
    try (Connection connection = dataSource.getConnection()) {
      return connection.getMetaData().getURL();
    } catch (SQLException e) {
      return e.toString();
    }
  }
}

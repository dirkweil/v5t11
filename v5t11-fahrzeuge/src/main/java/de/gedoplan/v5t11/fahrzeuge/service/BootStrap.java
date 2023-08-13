package de.gedoplan.v5t11.fahrzeuge.service;

import de.gedoplan.v5t11.util.jsf.NavigationPresenter;

import java.sql.Connection;
import java.sql.SQLException;

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.event.Observes;
import javax.sql.DataSource;

import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

@Dependent
public class BootStrap {

  void boot(@Observes StartupEvent startupEvent,
    Logger log,
    ConfigService configService,
    DataSource dataSource,
    @ConfigProperty(name = "kafka.bootstrap.servers", defaultValue = "(dev service; see url above)") String kafkaUrl,
    DbInitService dbInitService,
    JoinService joinService,
    NavigationPresenter navigationPresenter) {
    log.infof("app: %s:%s", configService.getArtifactId(), configService.getVersion());

    log.infof("configDir: %s", configService.getConfigDir());
    log.infof("anlage: %s", configService.getAnlage());
    log.infof("db: %s", getDbUrl(dataSource));
    log.infof("kafka: %s", kafkaUrl);
    log.infof("statusRestUrl: %s", configService.getStatusRestUrl());

    dbInitService.fillDb();

    joinService.joinMyself();
  }

  private String getDbUrl(DataSource dataSource) {
    try (Connection connection = dataSource.getConnection()) {
      return connection.getMetaData().getURL();
    } catch (SQLException e) {
      return e.toString();
    }
  }
}

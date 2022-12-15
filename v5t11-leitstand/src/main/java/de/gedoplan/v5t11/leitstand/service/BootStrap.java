package de.gedoplan.v5t11.leitstand.service;

import de.gedoplan.v5t11.leitstand.entity.Leitstand;
import de.gedoplan.v5t11.stellwerk.StellwerkUIStarter;
import de.gedoplan.v5t11.util.jsf.NavigationPresenter;
import io.quarkus.runtime.Quarkus;
import io.quarkus.runtime.StartupEvent;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import javax.enterprise.context.Dependent;
import javax.enterprise.event.Observes;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.stream.Collectors;

@Dependent
public class BootStrap {

  void boot(@Observes StartupEvent startupEvent,
    Logger log,
    ConfigService configService,
    Leitstand leitstand,
    StellwerkUIStarter stellwerkUIStarter,
    NavigationPresenter navigationPresenter,
    DataSource dataSource,
    @ConfigProperty(name = "kafka.bootstrap.servers", defaultValue = "(dev service; see url above)") String kafkaUrl,
    @ConfigProperty(name = "stellwerk-ui.enabled", defaultValue = "true") boolean stellwerkUIEnabled) {

    log.infof("app: %s:%s", configService.getArtifactId(), configService.getVersion());

    log.infof("configDir: %s", configService.getConfigDir());
    log.infof("anlage: %s", configService.getAnlage());
    log.infof("db: %s", getDbUrl(dataSource));
    log.infof("kafka: %s", kafkaUrl);
    log.infof("statusRestUrl: %s", configService.getStatusRestUrl());
    log.infof("fahrstrassenRestUrl: %s", configService.getFahrstrassenRestUrl());
    log.infof("bereiche: %s", leitstand.getBereiche().stream().collect(Collectors.joining(",")));

    if (stellwerkUIEnabled) {
      try {
        stellwerkUIStarter.start();
      } catch (Exception e) {
        log.error("Kann UI nicht starten", e);

        Quarkus.asyncExit(1);
      }
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

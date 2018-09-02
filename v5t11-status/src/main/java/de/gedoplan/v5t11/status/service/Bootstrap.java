package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Steuerung;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

@Dependent
public class Bootstrap {

  void boot(@Observes @Initialized(ApplicationScoped.class) Object object, ConfigService configService, Steuerung steuerung, SelectrixGateway selectrixGateway) {
    // Falls PortSpeed nicht angegeben, Default vom Interface holen
    int portSpeed = configService.getPortSpeed();
    if (portSpeed <= 0) {
      portSpeed = steuerung.getSxInterface().getSpeed();
    }

    // Interface starten
    selectrixGateway.start(configService.getPortName(), portSpeed, steuerung.getSxInterface().getTyp(), steuerung.getAdressen());

    // Aktuelle Werte einmal explizit holen
    steuerung.getAdressen().forEach(adr -> steuerung.setKanalWert(adr, selectrixGateway.getValue(adr, true), false));
  }

  void shutdown(@Observes @Destroyed(ApplicationScoped.class) Object object, SelectrixGateway selectrixGateway) {
    selectrixGateway.stop();
  }
}

package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Steuerung;
import de.gedoplan.v5t11.util.config.ConfigUtil;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

@Dependent
public class Bootstrap {

  void boot(@Observes @Initialized(ApplicationScoped.class) Object object, Steuerung steuerung, SelectrixGateway selectrixGateway) {
    // Falls PortSpeed nicht angegeben, Default vom Interface holen
    int portSpeed = ConfigUtil.getPortSpeed();
    if (portSpeed <= 0) {
      portSpeed = steuerung.getSxInterface().getSpeed();
    }

    // Interface starten
    selectrixGateway.start(ConfigUtil.getPortName(), portSpeed, steuerung.getSxInterface().getTyp(), steuerung.getAdressen());

    // Aktuelle Werte einmal explizit holen
    steuerung.getAdressen().forEach(adr -> steuerung.setKanalWert(adr, selectrixGateway.getValue(adr, true), false));
  }

  void shutdown(@Observes @Destroyed(ApplicationScoped.class) Object object, SelectrixGateway selectrixGateway) {
    selectrixGateway.stop();
  }
}

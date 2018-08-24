package de.gedoplan.v5t11.status.service;

import de.gedoplan.v5t11.status.entity.Steuerung;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.Dependent;
import javax.enterprise.context.Destroyed;
import javax.enterprise.context.Initialized;
import javax.enterprise.event.Observes;

@Dependent
public class Bootstrap {

  void boot(@Observes @Initialized(ApplicationScoped.class) Object object, Steuerung steuerung, SelectrixGateway selectrixGateway) {
    selectrixGateway.start("auto", -1, "selectrix", steuerung.getAdressen());
  }

  void shutdown(@Observes @Destroyed(ApplicationScoped.class) Object object, SelectrixGateway selectrixGateway) {
    selectrixGateway.stop();
  }
}

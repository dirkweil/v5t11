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

@Dependent
public class Bootstrap {

  @Resource
  ManagedExecutorService executorService;

  void boot(@Observes @Initialized(ApplicationScoped.class) Object object, Steuerung steuerung) {
    steuerung.injectFields();
    steuerung.open(this.executorService != null ? this.executorService : Executors.newSingleThreadExecutor());
  }

  void shutdown(@Observes @Destroyed(ApplicationScoped.class) Object object, Steuerung steuerung) {
    steuerung.close();
  }
}

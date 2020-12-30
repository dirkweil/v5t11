package de.gedoplan.v5t11.status.webui;

import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.cdi.Changed;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.inject.Named;

@Named
@ApplicationScoped
public class PushService {

  @Inject
  @Push
  PushContext uiRefresh;

  void fahrzeugChanged(@ObservesAsync @Changed Fahrzeug fahrzeug) {
    this.uiRefresh.send("fahrzeug");
  }

  void signalChanged(@ObservesAsync @Changed Signal signal) {
    this.uiRefresh.send("signal");
  }

  void weicheChanged(@ObservesAsync @Changed Weiche weiche) {
    this.uiRefresh.send("weiche");
  }

  void zentraleChanged(@ObservesAsync @Changed Zentrale zentrale) {
    this.uiRefresh.send("zentrale");
  }

}

package de.gedoplan.v5t11.fahrzeuge.webui;

import de.gedoplan.v5t11.fahrzeuge.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.cdi.Changed;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.ObservesAsync;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

@Named
@ApplicationScoped
public class PushService {

  @Inject
  @Push
  PushContext fahrzeugChanged;

  @Inject
  Logger logger;

  void fahrzeugChanged(@ObservesAsync @Changed Fahrzeug fahrzeug) {
    String msg = getEventName(fahrzeug);
    this.logger.debugf("Push fahrzeugChanged: %s", msg);
    this.fahrzeugChanged.send(msg);
  }

  public String getEventName(Fahrzeug fahrzeug) {
    return "_" + fahrzeug.getId().getAdresse() + "_" + fahrzeug.getId().getSystemTyp().name();
  }
}

package de.gedoplan.v5t11.status.webui;

import de.gedoplan.v5t11.status.entity.baustein.Zentrale;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Signal;
import de.gedoplan.v5t11.status.entity.fahrweg.geraet.Weiche;
import de.gedoplan.v5t11.status.entity.fahrzeug.Fahrzeug;
import de.gedoplan.v5t11.util.cdi.Changed;
import de.gedoplan.v5t11.util.jsf.UiRefreshService;
import io.vertx.core.impl.ConcurrentHashSet;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.event.ObservesAsync;
import javax.faces.event.WebsocketEvent;
import javax.faces.push.Push;
import javax.faces.push.PushContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ApplicationScoped
public class PushService {

  @Inject
  UiRefreshService uiRefreshService;

  void fahrzeugChanged(@ObservesAsync @Changed Fahrzeug fahrzeug) {
    this.uiRefreshService.send("fahrzeug");
  }

  void signalChanged(@ObservesAsync @Changed Signal signal) {
    this.uiRefreshService.send("signal");
  }

  void weicheChanged(@ObservesAsync @Changed Weiche weiche) {
    this.uiRefreshService.send("weiche");
  }

  void zentraleChanged(@ObservesAsync @Changed Zentrale zentrale) {
    this.uiRefreshService.send("zentrale");
  }

}
